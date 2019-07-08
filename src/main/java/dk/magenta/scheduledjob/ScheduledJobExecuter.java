package dk.magenta.scheduledjob;

import dk.magenta.beans.StatBean;
import org.alfresco.service.ServiceRegistry;
import org.apache.james.mime4j.dom.datetime.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class ScheduledJobExecuter {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledJobExecuter.class);

    /**
     * Public API access
     */
    private ServiceRegistry serviceRegistry;

    public void setStatBean(StatBean statBean) {
        this.statBean = statBean;
    }

    private StatBean statBean;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * Executer implementation
     */
    public void execute() {
        LOG.info("Running the scheduled job");

        int created = statBean.query("creationDate");
        System.out.println("number created:" + created);

        int closed = statBean.query("closedDate");
        System.out.println("number closed:" + closed);

        System.out.println("øh" + new Date());

        statBean.writeToDocument();

        // Work/Job implementation goes here...
    }
}