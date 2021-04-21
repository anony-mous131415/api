package com.komli.prime.service.reporting.jmx;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleAgent {

    private static final Logger logger = LoggerFactory.getLogger(SimpleAgent.class);
    private MBeanServer mbs = null;
    
    public SimpleAgent() {
        mbs = ManagementFactory.getPlatformMBeanServer();
        
        ObjectName repName = null;
        try {
            SLRerportingData repBean = new SLRerportingData();
            repName = new ObjectName("com.komli.prime.service.reporting.jmx:type=SLReportingMBean");
            StandardMBean mbean = new StandardMBean(repBean, SLReportingMBean.class);
            mbs.registerMBean(mbean, repName);
            logger.info("Registered SLReporting MBean.");
        } catch (Exception e) {
            logger.error("Exception Occured while registering SLReportingMBean", e);
        }
    }
    
}
