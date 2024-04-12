package dk.magenta.webscripts.contents;

import dk.magenta.beans.DocumentTemplateBean;
import dk.magenta.model.DatabaseModel;
import dk.magenta.utils.JSONUtils;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

public class MergeSuppleredendeUdtTemplate extends AbstractWebScript {

    public void setDocumentTemplateBean(DocumentTemplateBean documentTemplateBean) {
        this.documentTemplateBean = documentTemplateBean;
    }

    private DocumentTemplateBean documentTemplateBean;
    private JSONObject result;
    Writer webScriptWriter;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    private NodeService nodeService;


    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {
        Content c = webScriptRequest.getContent();
        JSONObject json = null;

        try {
            json = new JSONObject(c.getContent());

        webScriptResponse.setContentEncoding("UTF-8");
        webScriptWriter = webScriptResponse.getWriter();

        System.out.println("immer hefe snitzhel2");

        NodeRef newDocument = documentTemplateBean.generateSuppleredeUdtalelseDocument(new NodeRef("workspace://SpacesStore/" + (String)json.get("id")));

        //  RITM0879283  -- if declaration contains no psykolog skabelon - make one available. Contains a bug, searchs in the wrong dir. not recursively

        // hent noderef for folderen Erklæring og psykologisk undersøgelse
        NodeRef checkFolder = nodeService.getChildByName(new NodeRef("workspace://SpacesStore/" + (String)json.get("id")), ContentModel.ASSOC_CONTAINS, DatabaseModel.ATTR_DEFAULT_DECLARATION_FOLDER);

        String cpr =  (String)nodeService.getProperty(new NodeRef("workspace://SpacesStore/" + (String)json.get("id")), DatabaseModel.PROP_CPR);
        String fileName = cpr.substring(0,6) + "_psykundersøgelse.odt";

            System.out.println("hvad er fileName");
            System.out.println(fileName);

        List<String> query = Arrays.asList(fileName);
        List<ChildAssociationRef> children = nodeService.getChildrenByName(checkFolder, ContentModel.ASSOC_CONTAINS, query);

            System.out.println("children.size()");
            System.out.println(children.size());

        // if no document with query name exists, then make the psyk. document.
        if (children.size() == 0) {
            boolean bua = nodeService.hasAspect(new NodeRef("workspace://SpacesStore/" + (String)json.get("id")), DatabaseModel.ASPECT_BUA);
            if (!bua) {
                try {
                    documentTemplateBean.generatePSYKUS(new NodeRef("workspace://SpacesStore/" + (String)json.get("id")));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        result = JSONUtils.getObject("id", newDocument.getId());
        JSONUtils.write(webScriptWriter, result);

        AuthenticationUtil.clearCurrentSecurityContext();

        }
        catch (org.alfresco.service.cmr.model.FileExistsException e) {
            result = JSONUtils.getError("document already exists");
            JSONUtils.write(webScriptWriter, result);
        }
        catch (Exception e) {
            e.printStackTrace();
            result = JSONUtils.getError(new Exception(e.toString()));
            JSONUtils.write(webScriptWriter, result);

        }

    }

}
