/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec2Timeout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Web application lifecycle listener.
 *
 * @author ghsmith
 */
@WebListener()
public class ContextListener implements ServletContextListener {

    Thread t;
    
    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        
        java.lang.System.setProperty("aws.accessKeyId","XXXXXXXXXXXXXXXXXXXX");
        java.lang.System.setProperty("aws.secretKey","XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        sce.getServletContext().setAttribute("ec2InstanceId", "i-XXXXXXXXXXXXXXXXX");
        sce.getServletContext().setAttribute("startStopLog", new ArrayList());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 240);
        sce.getServletContext().setAttribute("ec2ShutdownTime", cal.getTime());

        ((ArrayList)sce.getServletContext().getAttribute("startStopLog")).add(Calendar.getInstance().getTime() + ": monitoring service started, instance state is " + Ec2.getInstanceState((String)sce.getServletContext().getAttribute("ec2InstanceId")));
        
        Runnable r = new Runnable() {
            public void run() {
                try {
                    while(true) {
                        try {
                            Thread.sleep(60000);
                            if(Calendar.getInstance().getTime().compareTo((Date)sce.getServletContext().getAttribute("ec2ShutdownTime")) > 0) {
                                String instanceId = (String)sce.getServletContext().getAttribute("ec2InstanceId");
                                if("running".equals(Ec2.getInstanceState(instanceId))) {
                                    Ec2.stopInstance(instanceId);
                                    ((ArrayList)sce.getServletContext().getAttribute("startStopLog")).add(Calendar.getInstance().getTime() + ": stopped");
                                }
                            }
                        }
                        catch (InterruptedException ex) {
                            throw ex;
                        }
                    }
                }
                catch(Exception ex) {
                    ((ArrayList)sce.getServletContext().getAttribute("startStopLog")).add(Calendar.getInstance().getTime() + ": monitoring service stopped");
                    throw new RuntimeException(ex);
                }
            }
        };
        
        t = new Thread(r);
        t.start();
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
        t.stop();
        
    }

}
