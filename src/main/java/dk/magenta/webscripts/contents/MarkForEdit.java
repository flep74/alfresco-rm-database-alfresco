package dk.magenta.webscripts.contents;

import dk.magenta.beans.MailBean;
import dk.magenta.model.DatabaseModel;
import dk.magenta.utils.JSONUtils;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class MarkForEdit extends AbstractWebScript {

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    private NodeService nodeService;

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    private PersonService personService;
    private AuthenticationService authenticationService;

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {


        webScriptResponse.setContentEncoding("UTF-8");
        Content c = webScriptRequest.getContent();
        Writer webScriptWriter = webScriptResponse.getWriter();
        JSONObject result;

        JSONObject json = null;
        try {

            System.out.println("the json" + c.getContent());
            json = new JSONObject(c.getContent());

            String method = (String)json.get("method");
            String nodeRef = (String)json.get("nodeRef");

            if (method.equals("add")) {

                String username = authenticationService.getCurrentUserName();
                NodeRef user = personService.getPerson(username);

                Map<QName, Serializable> aspectProps = new HashMap<>();
                aspectProps.put(DatabaseModel.PROP_MARKEDBY, user);

                nodeService.addAspect(new NodeRef(nodeRef), DatabaseModel.ASPECT_DECLARATIONMARKEDFOREDIT, aspectProps);
                result = JSONUtils.getSuccess();
                JSONUtils.write(webScriptWriter, result);
            }
            else if (method.equals("remove")) {
                nodeService.removeAspect(new NodeRef(nodeRef), DatabaseModel.ASPECT_DECLARATIONMARKEDFOREDIT);
                result = JSONUtils.getSuccess();
                JSONUtils.write(webScriptWriter, result);
            }
            else  if (method.equals("state")) {
                JSONObject jsonObject = new JSONObject();

                if (nodeService.hasAspect(new NodeRef(nodeRef), DatabaseModel.ASPECT_DECLARATIONMARKEDFOREDIT)) {

                    NodeRef person = (NodeRef)nodeService.getProperty(new NodeRef(nodeRef), DatabaseModel.PROP_MARKEDBY);
                    System.out.println("hvad er personNode");
                    System.out.println(person);

                    // only lock, if its not the same person editing, if the same, it means that the user or system didnt succeed unlocking when leaving libreoffice online
                    String currentUser = authenticationService.getCurrentUserName();
                    PersonService.PersonInfo personInfo = personService.getPerson(person);

                    if (!personInfo.getUserName().equals(currentUser)) {
                        jsonObject.put("state", nodeService.hasAspect(new NodeRef(nodeRef), DatabaseModel.ASPECT_DECLARATIONMARKEDFOREDIT));
                        jsonObject.put("firstName", personInfo.getFirstName());
                        jsonObject.put("lastName", personInfo.getLastName());
                    }
                    else {
                        nodeService.removeAspect(new NodeRef(nodeRef), DatabaseModel.ASPECT_DECLARATIONMARKEDFOREDIT);
                        jsonObject.put("state", false);
                    }
                }


                result = jsonObject;
                JSONUtils.write(webScriptWriter, result);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            result = JSONUtils.getError(e);
            webScriptResponse.setStatus(400);
            JSONUtils.write(webScriptWriter, result);
        }
    }
}
