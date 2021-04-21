/*
 *  Copyright 2012 Komli Media Inc. All Rights Reserved.
 *  KOMLI MEDIA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *  
 *  @version     1.0, 08-Aug-2012
 *  @author Rajat Bhushan
 */
package io.revx.core.model.targetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TargetSegments {

    public boolean               selectAllSegments;
    public List<Long>         includedSegments;
    public List<Long>         excludedSegments;
    public Map<Long, Segment> partiallySelectedSegments;

    public TargetSegments() {
        includedSegments = new ArrayList<>();
        excludedSegments = new ArrayList<>();
        partiallySelectedSegments = new HashMap<>();
    }
}
