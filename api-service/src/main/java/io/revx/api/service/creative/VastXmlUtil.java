package io.revx.api.service.creative;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import io.revx.core.exception.ApiException;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.creative.CompanionAdDetails;
import io.revx.core.model.creative.VastCreative;
import io.revx.core.model.creative.VideoCreativeVastXmlTemplate;
import io.revx.core.model.creative.VideoDetails;
import io.revx.core.model.creative.VideoUploadType;

@Component
public class VastXmlUtil {
  private static final Logger logger = LoggerFactory.getLogger(VastXmlUtil.class);

  public StringBuilder generateVastXml(List<String> thirdPartyImpressionTrackerList,
      VideoUploadType videoUploadType, VideoCreativeVastXmlTemplate videoCreativeTemplateData,
      VastCreative vastCreative, String vastCreativeName) throws ApiException {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder;

      dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.newDocument();
      Map<String, String> vastAttributes = new HashMap<String, String>();
      vastAttributes.put("version", "3.0");
      Element vastRootElement = getTag(doc, "VAST", null, vastAttributes);
      doc.appendChild(vastRootElement);
      Set<String> impressionUrls = new HashSet<String>();
      impressionUrls.add("https://trk.atomex.net/cgi-bin/tracker.fcgi/imp?|PAYLOAD||IMP_EVENT|");

      if (thirdPartyImpressionTrackerList != null && !thirdPartyImpressionTrackerList.isEmpty()) {
        for (String impressionTracker : thirdPartyImpressionTrackerList) {
          impressionUrls.add(impressionTracker);
        }
      }

      vastRootElement.appendChild(getAdElement(doc, impressionUrls, videoUploadType,
          videoCreativeTemplateData, vastCreative, vastCreativeName));

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      DOMSource source = new DOMSource(doc);
      StringBuilderWriter writer = new StringBuilderWriter();
      transformer.transform(source, new StreamResult(writer));
      logger.debug("Xml generated unescaped with dummykey: {}", writer.getBuilder().toString());
      /*
       * We couldn't add this macro inside Linear tag attributes. So added dummy property and replaceing it with macro here.
       */
      String xml = writer.toString().replace("<TPT_IMP_TRACKERS/>", "|TPT_IMP_TRACKERS|").replace("DummyKey=\"DummyValue\"","|SKIP_OFFSET|");
      return new StringBuilder(xml);
    } catch (Exception e) {
      logger.error("Not able to generate xml: " + ExceptionUtils.getStackTrace(e));
      throw new ApiException(ErrorCode.VAST_FORMATION_EXCEPTION ,
              new Object[] {"Error while creating vast xml."});
    }
    // return new StringBuilder();
  }



  private Element getTag(Document doc, String tagName, String textContent,
      Map<String, String> attributes) {
    Element tagWithText = doc.createElement(tagName);

    if (textContent != null && textContent.length() > 0) {
      tagWithText.setTextContent(textContent);
    }

    if (attributes != null && attributes.size() > 0) {
      for (Map.Entry<String, String> entry : attributes.entrySet()) {
        tagWithText.setAttribute(entry.getKey(), entry.getValue());
      }
    }
    return tagWithText;
  }



  private Element getAdElement(Document doc, Set<String> impressionUrls,
      VideoUploadType videoUploadType, VideoCreativeVastXmlTemplate videoCreativeTemplateData,
      VastCreative vastCreative, String vastCreativeName) {
    Map<String, String> adAttributes = new HashMap<String, String>();
    Element adTag = null;
    if (videoUploadType == VideoUploadType.VIDEO) {
      adAttributes.put("id", videoCreativeTemplateData.getCreativeName());
      adTag = getTag(doc, "Ad", null, adAttributes);
      adTag.appendChild(getInlineElement(doc, impressionUrls, videoCreativeTemplateData));
    } else if (videoUploadType == VideoUploadType.VAST_WRAPPER) {
      adAttributes.put("id", vastCreativeName);
      adTag = getTag(doc, "Ad", null, adAttributes);
      adTag.appendChild(getWrapperElement(doc, impressionUrls, vastCreative));
    }
    return adTag;
  }



  private Element getInlineElement(Document doc, Set<String> impressionUrls,
      VideoCreativeVastXmlTemplate videoCreativeTemplateData) {
    Element inlineTag = getTag(doc, "InLine", null, null);
    inlineTag.appendChild(getTag(doc, "AdSystem", "RevX VAST Template", null));
    inlineTag.appendChild(getTag(doc, "AdTitle", "RevX VAST Tag", null));
    inlineTag.appendChild(getErrorTag(doc));
    for (String impressionUrl : impressionUrls) {
      Element impressionTag = getTag(doc, "Impression", null, null);
      Node impTrackingURL = doc.createCDATASection(impressionUrl);
      impressionTag.appendChild(impTrackingURL);
      inlineTag.appendChild(impressionTag);
    }
    Element impTrackingURLMacro = doc.createElement("TPT_IMP_TRACKERS");
    inlineTag.appendChild(impTrackingURLMacro);
    inlineTag.appendChild(getCreativesTag(doc, VideoUploadType.VIDEO, videoCreativeTemplateData));
    inlineTag.appendChild(getExtensionsTag(doc));
    return inlineTag;
  }


  private Element getWrapperElement(Document doc, Set<String> impressionUrls,
      VastCreative vastCreative) {
    Element wrapperTag = getTag(doc, "Wrapper", null, null);
    wrapperTag.appendChild(getTag(doc, "AdSystem", "RevX VAST Template", null));
    wrapperTag.appendChild(getErrorTag(doc));

    for (String impressionUrl : impressionUrls) {
      Element impressionTag = getTag(doc, "Impression", null, null);
      Node impTrackingURL = doc.createCDATASection(impressionUrl);
      impressionTag.appendChild(impTrackingURL);
      wrapperTag.appendChild(impressionTag);
    }

    Element vastAdTagURITag = getTag(doc, "VASTAdTagURI", null, null);
    Node vastAdTagURI = doc.createCDATASection(vastCreative.getVideoLink());
    vastAdTagURITag.appendChild(vastAdTagURI);
    wrapperTag.appendChild(vastAdTagURITag);
    wrapperTag.appendChild(getCreativesTag(doc, VideoUploadType.VAST_WRAPPER, null));
    return wrapperTag;
  }

  private Element getErrorTag(Document doc) {
    Element errorTag = getTag(doc, "Error", null, null);
    Node errorTrackingURL = doc.createCDATASection(
        "https://trk.atomex.net/cgi-bin/tracker.fcgi/imp?|PAYLOAD|&vec=[ERRORCODE]");
    errorTag.appendChild(errorTrackingURL);
    return errorTag;
  }



  private Element getCreativesTag(Document doc, VideoUploadType videoUploadType,
      VideoCreativeVastXmlTemplate videoCreativeTemplateData) {
    Element creatives = getTag(doc, "Creatives", null, null);

    if (videoUploadType == VideoUploadType.VIDEO) {
      if (videoCreativeTemplateData.getVideos() != null
          && videoCreativeTemplateData.getVideos().size() > 0) {
        Element creative = getTag(doc, "Creative", null, null);
        creative.appendChild(getLinearForCreative(doc, videoCreativeTemplateData.getDuration(),
            videoCreativeTemplateData.getVideos()));
        creatives.appendChild(creative);
      }

      if ((videoCreativeTemplateData.getCompanionAds() != null
          && videoCreativeTemplateData.getCompanionAds().size() > 0)) {
        creatives.appendChild(
            getCreativeForCompanion(doc, videoCreativeTemplateData.getCompanionAds()));
      }
    } else if (videoUploadType == VideoUploadType.VAST_WRAPPER) {
      Element creative = getTag(doc, "Creative", null, null);
      Element linearTag = getTag(doc, "Linear", null, null);
      linearTag.appendChild(getTrackingEvents(doc));

      Element videoClicks = getTag(doc, "VideoClicks", null, null);
      Element clickTrackingTag = getTag(doc, "ClickTracking", null, null);
      Node clickTrackingURL = doc.createCDATASection(
          "https://trk.atomex.net/cgi-bin/tracker.fcgi/clk?click=videoClickTracking&|PAYLOAD|");
      clickTrackingTag.appendChild(clickTrackingURL);
      videoClicks.appendChild(clickTrackingTag);

      linearTag.appendChild(videoClicks);
      creative.appendChild(linearTag);
      creatives.appendChild(creative);
    }
    return creatives;
  }


  private Element getLinearForCreative(Document doc, long duration, Set<VideoDetails> videos) {
    Element linearTag = getTag(doc, "Linear", null, null);
    /*
     * Adding below attribute just to make valid xml, while storing in DB we will replace with
     * |SKIP_OFFSET|
     */
    linearTag.setAttribute("DummyKey", "DummyValue");
    linearTag.appendChild(getTag(doc, "Duration", durationInFormat(duration), null));
    linearTag.appendChild(getTrackingEvents(doc));
    linearTag.appendChild(getVideoClicksTag(doc));
    linearTag.appendChild(getMediaFilesTag(doc, videos));

    return linearTag;
  }


  private Element getMediaFilesTag(Document doc, Set<VideoDetails> videos) {
    Element mediaFiles = getTag(doc, "MediaFiles", null, null);
    for (VideoDetails videoDetails : videos) {
      Map<String, String> mediaFileAttributes = new HashMap<>();
      mediaFileAttributes.put("delivery", videoDetails.getDelivery() == null || videoDetails.getDelivery().isEmpty() ? "progressive": videoDetails.getDelivery());
      mediaFileAttributes.put("type", videoDetails.getVideoFormat().getXmlAttributeValue());
      mediaFileAttributes.put("bitrate", videoDetails.getBitrate().toString());
      mediaFileAttributes.put("width", videoDetails.getSize().width.toString());
      mediaFileAttributes.put("height", videoDetails.getSize().height.toString());

      Element mediaFile = getTag(doc, "MediaFile", null, mediaFileAttributes);
      Node mediaFileLink = doc.createCDATASection(videoDetails.getVideoLink());
      mediaFile.appendChild(mediaFileLink);
      mediaFiles.appendChild(mediaFile);
    }

    return mediaFiles;
  }



  private String durationInFormat(Long seconds) {
    Date d = new Date(seconds * 1000L);
    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss"); // HH for 0-23
    df.setTimeZone(TimeZone.getTimeZone("GMT"));
    String time = df.format(d);
    return time;
  }


  private Element getVideoClicksTag(Document doc) {
    Element videoClicks = getTag(doc, "VideoClicks", null, null);
    Element clickThroughTag = getTag(doc, "ClickThrough", null, null);
    Node clickThroughURL = doc.createCDATASection(
        "https://trk.atomex.net/cgi-bin/tracker.fcgi/clk?click=videoClickThrough&|VIDEO_CLK_PAYLOAD|");
    clickThroughTag.appendChild(clickThroughURL);
    videoClicks.appendChild(clickThroughTag);
    return videoClicks;
  }

  private Element getTrackingEvents(Document doc) {
    Element trackingEvents = doc.createElement("TrackingEvents");
    Map<String, String> eventDetails = new HashMap<String, String>();

    eventDetails.put("event", "start");
    trackingEvents.appendChild(getTrackingEventURL(doc, 1, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "firstQuartile");
    trackingEvents.appendChild(getTrackingEventURL(doc, 2, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "midpoint");
    trackingEvents.appendChild(getTrackingEventURL(doc, 3, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "thirdQuartile");
    trackingEvents.appendChild(getTrackingEventURL(doc, 4, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "complete");
    trackingEvents.appendChild(getTrackingEventURL(doc, 5, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "skip");
    trackingEvents.appendChild(getTrackingEventURL(doc, 6, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "mute");
    trackingEvents.appendChild(getTrackingEventURL(doc, 11, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "unmute");
    trackingEvents.appendChild(getTrackingEventURL(doc, 12, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "pause");
    trackingEvents.appendChild(getTrackingEventURL(doc, 13, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "rewind");
    trackingEvents.appendChild(getTrackingEventURL(doc, 14, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "resume");
    trackingEvents.appendChild(getTrackingEventURL(doc, 15, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "fullscreen");
    trackingEvents.appendChild(getTrackingEventURL(doc, 16, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "exitFullscreen");
    trackingEvents.appendChild(getTrackingEventURL(doc, 17, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "close");
    trackingEvents.appendChild(getTrackingEventURL(doc, 18, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "closeLinear");
    trackingEvents.appendChild(getTrackingEventURL(doc, 19, eventDetails));
    
    /*
     * Removing this progress event as first/mid/third quartile is enough for tracking.
    eventDetails.clear();
    eventDetails.put("event", "progress");
    eventDetails.put("offset", "10%");
    trackingEvents.appendChild(getTrackingEventURL(doc, 21, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "progress");
    eventDetails.put("offset", "20%");
    trackingEvents.appendChild(getTrackingEventURL(doc, 22, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "progress");
    eventDetails.put("offset", "30%");
    trackingEvents.appendChild(getTrackingEventURL(doc, 23, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "progress");
    eventDetails.put("offset", "40%");
    trackingEvents.appendChild(getTrackingEventURL(doc, 24, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "progress");
    eventDetails.put("offset", "60%");
    trackingEvents.appendChild(getTrackingEventURL(doc, 25, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "progress");
    eventDetails.put("offset", "70%");
    trackingEvents.appendChild(getTrackingEventURL(doc, 26, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "progress");
    eventDetails.put("offset", "80%");
    trackingEvents.appendChild(getTrackingEventURL(doc, 27, eventDetails));

    eventDetails.clear();
    eventDetails.put("event", "progress");
    eventDetails.put("offset", "90%");
    trackingEvents.appendChild(getTrackingEventURL(doc, 28, eventDetails));
    */
    return trackingEvents;
  }



  private Element getExtensionsTag(Document doc) {
    Element extensions = getTag(doc, "Extensions", null, null);
    Map<String, String> extensionAttributes = new HashMap<String, String>();
    extensionAttributes.put("type", "MoPub");
    Element extension = getTag(doc, "Extension", null, extensionAttributes);
    Element moPubCtaText = getTag(doc, "MoPubCtaText", "INSTALL", null);
    Element moPubForceOrientation = getTag(doc, "MoPubForceOrientation", "Device", null);
    extension.appendChild(moPubCtaText);
    extension.appendChild(moPubForceOrientation);
    extensions.appendChild(extension);
    return extensions;
  }


  private Element getTrackingEventURL(Document doc, Integer eventId,
      Map<String, String> attributes) {
    Element trackingEvent = getTag(doc, "Tracking", null, attributes);
    Node trackingEventURL = doc.createCDATASection(
        "https://trk.atomex.net/cgi-bin/tracker.fcgi/imp?|PAYLOAD|&event=" + eventId);
    trackingEvent.appendChild(trackingEventURL);
    return trackingEvent;
  }

  private Element getCreativeForCompanion(Document doc,
      Set<CompanionAdDetails> companionAdDetails) {
    Element creative = getTag(doc, "Creative", null, null);
    Element companionAds = getTag(doc, "CompanionAds", null, null);
    creative.appendChild(companionAds);

    for (CompanionAdDetails companionAdDetail : companionAdDetails) {
      Element landscapeCompanion = getCompanion(doc, companionAdDetail,
          "https://trk.atomex.net/cgi-bin/tracker.fcgi/clk?click=companionClickThrough&|VIDEO_CLK_PAYLOAD|");
      companionAds.appendChild(landscapeCompanion);
    }
    
    return creative;
  }

  private Element getCompanion(Document doc, CompanionAdDetails companionAdDetails,
      String companionClickThroughURL) {

    Map<String, String> companionAttributes = new HashMap<String, String>();
    companionAttributes.put("width", companionAdDetails.getSize().width.toString());
    companionAttributes.put("height", companionAdDetails.getSize().height.toString());
    Element companion = getTag(doc, "Companion", null, companionAttributes);

    Map<String, String> staticResourceAttributes = new HashMap<String, String>();
    staticResourceAttributes.put("creativeType",
        companionAdDetails.getImageFormat().getXmlAttributeValue());
    Element staticResource = getTag(doc, "StaticResource", null, staticResourceAttributes);
    Node staticResourceLink = doc.createCDATASection(companionAdDetails.getImageLink());
    staticResource.appendChild(staticResourceLink);

    Element companionClickThrough = getTag(doc, "CompanionClickThrough", null, null);
    Node companionClickThroughURLNode = doc.createCDATASection(companionClickThroughURL);
    companionClickThrough.appendChild(companionClickThroughURLNode);

    companion.appendChild(staticResource);
    companion.appendChild(companionClickThrough);

    return companion;
  }

}
