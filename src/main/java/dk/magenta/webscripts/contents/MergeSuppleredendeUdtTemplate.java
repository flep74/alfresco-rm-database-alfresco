package dk.magenta.webscripts.contents;

import dk.magenta.beans.DocumentTemplateBean;
import dk.magenta.model.DatabaseModel;
import dk.magenta.utils.JSONUtils;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileFolderService;
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

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    private FileFolderService fileFolderService;


    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {
        Content c = webScriptRequest.getContent();
        JSONObject json = null;

        try {
            json = new JSONObject(c.getContent());

        webScriptResponse.setContentEncoding("UTF-8");
        webScriptWriter = webScriptResponse.getWriter();

        System.out.println("immer hefe snitzhel");

        NodeRef newDocument = documentTemplateBean.generateSuppleredeUdtalelseDocument(new NodeRef("workspace://SpacesStore/" + (String)json.get("id")));

        //  RITM0879283  -- if declaration contains no psykolog skabelon - make one available.

        String cpr =  (String)nodeService.getProperty(new NodeRef("workspace://SpacesStore/" + (String)json.get("id")), DatabaseModel.PROP_CPR);
        String fileName = cpr.substring(0,6) + "_psykundersøgelse.odt";
        List<String> query = Arrays.asList(fileName);

        NodeRef folder = fileFolderService.searchSimple(new NodeRef("workspace://SpacesStore/" + (String)json.get("id")), DatabaseModel.ATTR_DEFAULT_DECLARATION_FOLDER);
        List<ChildAssociationRef> children = nodeService.getChildrenByName(folder, ContentModel.ASSOC_CONTAINS, query);
            System.out.println("children");
            System.out.println(children);

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
