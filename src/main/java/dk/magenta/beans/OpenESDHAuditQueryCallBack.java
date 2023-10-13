package dk.magenta.beans;


import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.audit.AuditService;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.springframework.extensions.surf.util.I18NUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by flemmingheidepedersen on 18/11/14.
 */
public class OpenESDHAuditQueryCallBack implements AuditService.AuditQueryCallback {

    private Map<String, Boolean> validKeys;

    public JSONObject getResult() {
        System.out.println("result");
        System.out.println(result);
        return result;
    }

    private JSONObject result = new JSONObject();

    public OpenESDHAuditQueryCallBack() {
        super();
        System.out.println("OpenESDHAuditQueryCallBack2");
        try {
            result.put("entries", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getFullUserName(String user) {
        return "";
    }

    @Override
    public boolean valuesRequired() {
        return true;
    }

    @Override
    public boolean handleAuditEntry(Long entryId, String applicationName, String user, long time, Map<String, Serializable> values) {
        System.out.println("handleAuditEntry");
        System.out.println("handleAuditEntry");
        System.out.println("handleAuditEntry");


        JSONObject auditEntry = new JSONObject();
        try {
            auditEntry.put("user",user);
            auditEntry.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        breakableloop:
        for (Map.Entry<String, Serializable> entry : values.entrySet()) {

            String key = entry.getKey();
            Serializable value = entry.getValue();



            if (key != null && value != null) {
                System.out.println("key");
                System.out.println(key);

                switch (key) {

                    // file/folder CRUD transaction
                    case "/esdh/transaction/user":  {

                        if (values.get("/esdh/transaction/action").equals("CREATE")) {
                            if (validKeys.get("/esdh/transaction/action=CREATE")) {
                                String path = (String)values.get("/esdh/transaction/path");
                                String[] pArray = path.split("/");

                                if (path.indexOf("OpenESDHModel.DOCUMENTS_FOLDER_NAME") != -1) {
                                    try {
                                        auditEntry.put("action", I18NUtil.getMessage("auditlog.label.document.added") + " " + pArray[7].split(":")[1]);
                                        result.append("entries", auditEntry);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break breakableloop;
                                } else {
                                    try {
                                        auditEntry.put("action", I18NUtil.getMessage("auditlog.label.case.created") + " " + pArray[6].split(":")[1]);
                                        result.append("entries", auditEntry);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break breakableloop;
                                }
                            }
                        }
                        else if (values.get("/esdh/transaction/action").equals("DELETE")) {
                            if (validKeys.get("/esdh/transaction/action=DELETE")) {
                                String path = (String)values.get("/esdh/transaction/path");
                                String[] pArray = path.split("/");
                                //TODO check at which index the name of the file is located - 7 is wrong...

                                if (path.indexOf("OpenESDHModel.DOCUMENTS_FOLDER_NAME") != -1) {
                                    try {
                                        auditEntry.put("action", I18NUtil.getMessage("auditlog.label.document.added") + " " + pArray[7].split(":")[1]);
                                        result.append("entries", auditEntry);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break breakableloop;
                                } else {
                                    try {
                                        auditEntry.put("action", I18NUtil.getMessage("auditlog.label.case.created") + " " + pArray[6].split(":")[1]);
                                        result.append("entries", auditEntry);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break breakableloop;
                                }
                            }
                        }




                    }
                }

            }
        }
                   System.out.println(entryId + " " + applicationName + " " + user + " " +  new Date(time) + values);
        return true;
    }

    @Override
    public boolean handleAuditEntryError(Long entryId, String errorMsg, Throwable error) {

        throw new AlfrescoRuntimeException(errorMsg,error);
    }
}




