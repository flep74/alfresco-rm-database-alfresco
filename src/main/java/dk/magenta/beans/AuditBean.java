package dk.magenta.beans;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.template.TemplateNode;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import dk.magenta.model.DatabaseModel;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.audit.AuditQueryParameters;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.WebScriptException;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import static dk.magenta.model.DatabaseModel.*;

public class AuditBean {

    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

    private AuditService auditService;

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    private NamespaceService namespaceService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    private NodeService nodeService;

    // create auditQueryCallback inside this method, putting it outside, will make it a singleton as the class is a service.
    OpenESDHAuditQueryCallBack auditQueryCallback = new OpenESDHAuditQueryCallBack();



    public AuditBean() {
        auditQueryCallback = new OpenESDHAuditQueryCallBack();
    }

    public JSONObject getAuditLogByCaseNodeRef(String caseID, String auditFrom) {

        AuditQueryParameters auditQueryParameters = new AuditQueryParameters();
//        auditQueryParameters.setForward(true);
        auditQueryParameters.setApplicationName("RMDatabaseAccess");


        System.out.println("hvad er auditFrom");
        System.out.println(auditFrom);


        //convert String to LocalDate
        org.joda.time.LocalDate fromDate = new org.joda.time.LocalDate(auditFrom);
        System.out.println("hvad er from date");
        System.out.println(fromDate.toDate().getTime());

        System.out.println("og tilbage");
        System.out.println(new Date(fromDate.toDate().getTime()));
        System.out.println(new Date(fromDate.toDate().getTime()));
        System.out.println(new Date(fromDate.toDate().getTime()));




        org.joda.time.LocalDate localDate = new org.joda.time.LocalDate();
        org.joda.time.LocalDate yesterdayDate = localDate.minusDays(1);


        auditQueryParameters.setFromTime(fromDate.toDate().getTime());
//        auditQueryParameters.setFromTime((new Date(+).getTime()));
//        auditQueryParameters.addSearchKey(null, nodeRef.toString());
        auditQueryParameters.addSearchKey("/rm-database-access/post/NodeService/setProperty/no-error/case", caseID);



        // create auditQueryCallback inside this method, putting it outside, will make it a singleton as the class is a service.
        OpenESDHAuditQueryCallBack auditQueryCallback = new OpenESDHAuditQueryCallBack();

        try {
            auditService.auditQuery(auditQueryCallback, auditQueryParameters, 500);
            System.out.println("done");
        }
        catch (Exception e) {
            e.printStackTrace();
        }



        // test comment

        return auditQueryCallback.getResult();


    };


}


