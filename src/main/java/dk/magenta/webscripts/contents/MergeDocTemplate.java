package dk.magenta.webscripts.contents;

import dk.magenta.beans.DocumentTemplateBean;
import dk.magenta.beans.MailBean;
import dk.magenta.model.DatabaseModel;
import dk.magenta.utils.JSONUtils;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

public class MergeDocTemplate extends AbstractWebScript {

    public void setDocumentTemplateBean(DocumentTemplateBean documentTemplateBean) {
        this.documentTemplateBean = documentTemplateBean;
    }

    private DocumentTemplateBean documentTemplateBean;
    private JSONObject result;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    private NodeService nodeService;

    Writer webScriptWriter;



    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {
        Content c = webScriptRequest.getContent();
        JSONObject json = null;

        try {
            json = new JSONObject(c.getContent());

        webScriptResponse.setContentEncoding("UTF-8");
        webScriptWriter = webScriptResponse.getWriter();


        Map<String, String> params = JSONUtils.parseParameters(webScriptRequest.getURL());

        String nodeRef = params.get("nodeRef");
        NodeRef node = new NodeRef("workspace://SpacesStore/" + json.get("id"));

        String status = (String) nodeService.getProperty(node, DatabaseModel.PROP_STATUS);

            System.out.println("hvad er status");
            System.out.println(status);

        // todo check here for the state indlagt and handle accordingly and only for BUA
        if ( !(nodeService.hasAspect(node, DatabaseModel.ASPECT_BUA)) && (status.equals(DatabaseModel.statusIndlagt) || status.equals(DatabaseModel.statusIndlagtGR)) ) {

            String newDocument = documentTemplateBean.populateIndlagt(new NodeRef("workspace://SpacesStore/" + json.get("id")), (String) json.get("type"), (String) json.get("retten"), (String) json.get("dato"));
            result = JSONUtils.getObject("id", newDocument.toString());
            JSONUtils.write(webScriptWriter, result);

        }
        else if (json.has("type")) {

            AuthenticationUtil.setRunAsUserSystem();


            if (json.get("type").equals(DatabaseModel.PROP_TEMPLATE_DOC_KENDELSE)) {

                if (json.has("dato") && json.has("retten")) {
                    String newDocument = documentTemplateBean.populateDocument(new NodeRef("workspace://SpacesStore/" + json.get("id")), (String)json.get("type") , (String)json.get("retten"), (String)json.get("dato") );
                    result = JSONUtils.getObject("id", newDocument.toString());
                    JSONUtils.write(webScriptWriter, result);
                }
                else {
                    result = JSONUtils.getError(new Exception("wrong parameters supplied"));
                    JSONUtils.write(webScriptWriter, result);
                }
            }
            else {
                String newDocument = documentTemplateBean.populateDocument(new NodeRef("workspace://SpacesStore/" + json.get("id")), (String)json.get("type") , "", "" );
                result = JSONUtils.getObject("id", newDocument.toString());
                JSONUtils.write(webScriptWriter, result);
            }

            AuthenticationUtil.clearCurrentSecurityContext();




        }
        else {
            result = JSONUtils.getError(new Exception("wrong parameters supplied"));
            JSONUtils.write(webScriptWriter, result);
        }


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
