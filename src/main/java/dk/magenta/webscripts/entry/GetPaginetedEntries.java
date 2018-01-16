package dk.magenta.webscripts.entry;

import dk.magenta.beans.DatabaseBean;
import dk.magenta.beans.EntryBean;
import dk.magenta.utils.JSONUtils;
import dk.magenta.utils.QueryUtils;
import org.alfresco.service.cmr.repository.NodeRef;
import org.activiti.engine.impl.util.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class GetPaginetedEntries extends AbstractWebScript {

    private EntryBean entryBean;
    private DatabaseBean databaseBean;

    public void setEntryBean(EntryBean entryBean) {
        this.entryBean = entryBean;
    }
    public void setDatabaseBean(DatabaseBean databaseBean) {
        this.databaseBean = databaseBean;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

//        http://localhost:8080/alfresco/service/database/retspsyk/page_entries/?skip=0&maxItems=2&keyValue=[{%22key%22:%22lort%22,%20%22value%22:%22ihovedet%22}]

        Map<String, String> templateArgs = req.getServiceMatch().getTemplateVars();
        res.setContentEncoding("UTF-8");
        Writer webScriptWriter = res.getWriter();
        JSONObject result = new JSONObject();

        try {
            String siteShortName = templateArgs.get("siteShortName");
            String entryValue = templateArgs.get("entryValue");
            int skip = Integer.valueOf(req.getParameter("skip"));
            int maxItems = Integer.valueOf(req.getParameter("maxItems"));
            String keyValue = req.getParameter("keyValue");


            org.json.JSONArray entries = new org.json.JSONArray();


            String type = databaseBean.getType(siteShortName);
            String query = QueryUtils.getKeyValueQuery(siteShortName, type, new org.json.JSONArray(keyValue));

            List<NodeRef> nodeRefs = entryBean.getEntries(query, skip, maxItems);

            Iterator<NodeRef> i = nodeRefs.iterator();

            while (i.hasNext()) {
                NodeRef nodeRef = i.next();
                entries.put(entryBean.toJSON(nodeRef));
            }

            result.put("entries", entries);
            result.put("back", skip);
            result.put("next", skip + maxItems);
            result.put("total", nodeRefs.size());

        } catch (Exception e) {
            e.printStackTrace();
            result = JSONUtils.getError(e);
            res.setStatus(400);
        }

        JSONUtils.write(webScriptWriter, result);
    }
}

// F.eks. curl -i -u admin:admin -X GET 'http://localhost:8080/alfresco/s/database/retspsyk/entry/445644-4545-4564-8848-1849155'