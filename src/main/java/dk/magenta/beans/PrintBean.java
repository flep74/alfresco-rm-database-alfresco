package dk.magenta.beans;

import dk.magenta.model.DatabaseModel;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.Border;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.table.Column;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static dk.magenta.model.DatabaseModel.ASPECT_TMP;

public class PrintBean {

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    private SiteService siteService;
    private NodeService nodeService;
    private ContentService contentService;

    public void setTransformBean(TransformBean transformBean) {
        this.transformBean = transformBean;
    }

    private TransformBean transformBean;




    public String printEntriesToPDF(org.json.JSONArray entries, JSONObject searchQueriesForPdf) throws Exception {

        TextDocument document = TextDocument.newTextDocument();

//        Paragraph headline = document.addParagraph("udskrevet af... ");
        //headline.setHorizontalAlignment(StyleTypeDefinitions.HorizontalAlignmentType.CENTER);
        

        this.setupUsedSearchCriteries(searchQueriesForPdf, document.addTable(3,1));

        Table table = document.addTable();

        Row header = table.getRowByIndex(0);
        Row header2 = table.getRowByIndex(1);

        header.getCellByIndex(0).addParagraph("Nr.");
        header.getCellByIndex(1).addParagraph("Type");
        header.getCellByIndex(2).addParagraph("Navn");
        header.getCellByIndex(3).addParagraph("cpr");
        header.getCellByIndex(4).addParagraph("Læge");
        header.getCellByIndex(5).addParagraph("Tiltrædes af");
        header.getCellByIndex(6).addParagraph("Erklæring afgivet");
        header.getCellByIndex(7).addParagraph("Psykolog");
//
        Font headerFont = new Font("Arial", StyleTypeDefinitions.FontStyle.BOLD, 8, Color.BLACK);
        Border border = new Border(Color.BLACK, 0.1, StyleTypeDefinitions.SupportedLinearMeasure.PT);




        header.getCellByIndex(0).setFont(headerFont);
        header.getCellByIndex(1).setFont(headerFont);
        header.getCellByIndex(2).setFont(headerFont);
        header.getCellByIndex(3).setFont(headerFont);
        header.getCellByIndex(4).setFont(headerFont);
        header.getCellByIndex(5).setFont(headerFont);
        header.getCellByIndex(6).setFont(headerFont);
        header.getCellByIndex(7).setFont(headerFont);

        header.getCellByIndex(0).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
        header.getCellByIndex(1).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
        header.getCellByIndex(2).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
        header.getCellByIndex(3).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
        header.getCellByIndex(4).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
        header.getCellByIndex(5).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
        header.getCellByIndex(6).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
        header.getCellByIndex(7).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);

        header2.getCellByIndex(0).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
        header2.getCellByIndex(1).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
        header2.getCellByIndex(2).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
        header2.getCellByIndex(3).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
        header2.getCellByIndex(4).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
        header2.getCellByIndex(5).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
        header2.getCellByIndex(6).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
        header2.getCellByIndex(7).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);



        Font font = new Font("Arial", StyleTypeDefinitions.FontStyle.REGULAR, 8, Color.BLACK);

        for (int i=0; (i <= entries.length()-1); i++ ) {

            Row row = table.appendRow();


            JSONObject o = entries.getJSONObject(i);

            Integer caseNumber = (Integer)o.get("caseNumber");
            boolean bua = (boolean) o.get("bua");
            String fullname = (String)o.get("fullName");
            String cprNumber = (String)o.get("cprNumber");



            String doctor;
            if (o.has("doctor")) {
                doctor = (String)o.get("doctor");
            }
            else {
                doctor = "";
            }


            String superVisorDoctor;
            if (o.has("supervisingDoctor")) {
                superVisorDoctor = (String)o.get("supervisingDoctor");
            }
            else {
                superVisorDoctor = "";
            }

            boolean closed = false;
            String closedString = "";
            String closedWithoutDeclarationReason = "";

            boolean closedWithOutDeclaration = false;
            if (o.has("closedWithOutDeclaration") && (Boolean)o.get("closedWithOutDeclaration")) {
                closedWithOutDeclaration = true;
            }

            if (o.has("closedWithoutDeclarationReason")) {
                closedWithoutDeclarationReason = (String)o.get("closedWithoutDeclarationReason");
            }

            if (o.has("closed")) {
                closed = (Boolean)o.get("closed");

                if ( (!closed) && (o.has("declarationDate"))) {
                    closedString = "Afgivet, mangler afslutning";
                }
                else if ( (closed) && !(closedWithOutDeclaration)) {
                    closedString = "Afsluttet";
                }
                else if (closedWithOutDeclaration){
                    closedString = "Afsluttet uden erklæring: " + closedWithoutDeclarationReason;
                }
            }
            else {
                closedString = "";
            }



            String psychologist;
            if (o.has("psychologist")) {
                psychologist = (String)o.get("psychologist");
            }
            else {
                psychologist = "";
            }

            row.getCellByIndex(0).setFont(font);
            row.getCellByIndex(1).setFont(font);
            row.getCellByIndex(2).setFont(font);
            row.getCellByIndex(3).setFont(font);
            row.getCellByIndex(4).setFont(font);
            row.getCellByIndex(5).setFont(font);
            row.getCellByIndex(6).setFont(font);
            row.getCellByIndex(7).setFont(font);

            row.getCellByIndex(0).addParagraph(String.valueOf(caseNumber));
            row.getCellByIndex(1).addParagraph( bua ? "BUA" : "PS");
            row.getCellByIndex(2).addParagraph(fullname);
            row.getCellByIndex(3).addParagraph(cprNumber);
            row.getCellByIndex(4).addParagraph(doctor);
            row.getCellByIndex(5).addParagraph(superVisorDoctor);
            row.getCellByIndex(6).addParagraph(closedString);
            row.getCellByIndex(7).addParagraph(psychologist);

            row.getCellByIndex(0).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
            row.getCellByIndex(1).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
            row.getCellByIndex(2).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
            row.getCellByIndex(3).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
            row.getCellByIndex(4).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
            row.getCellByIndex(5).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
            row.getCellByIndex(6).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);
            row.getCellByIndex(7).setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, border);

        }

//        table.getColumnList().get(0).setWidth(15);
//        table.getColumnList().get(1).setWidth(10);
//        table.getColumnList().get(2).setWidth(25);
//        table.getColumnList().get(3).setWidth(10);
//        table.getColumnList().get(4).setWidth(10);
//        table.getColumnList().get(5).setWidth(10);
//        table.getColumnList().get(6).setWidth(30);
//        table.getColumnList().get(7).setWidth(30);


        File f = new File("tmp.odt");
        document.save(f);

        NodeRef tmpFolder = siteService.getContainer("retspsyk", DatabaseModel.PROP_TMP);

        // Create new PDF
        Map<QName, Serializable> tmpNodeProperties = new HashMap<>();
//        tmpNodeProperties.put(ContentModel.PROP_NAME, "tmpPrintNode");


        ChildAssociationRef tmpNode = nodeService.createNode(tmpFolder, ContentModel.ASSOC_CONTAINS,
                QName.createQName(ContentModel.USER_MODEL_URI, "theTempNode"),
                ContentModel.TYPE_CONTENT, tmpNodeProperties);



        ContentWriter writer = contentService.getWriter(tmpNode.getChildRef(), ContentModel.PROP_CONTENT, true);
        writer.setMimetype("application/vnd.oasis.opendocument.text");
        writer.putContent(f);


        NodeRef pdf = this.transform(tmpNode.getChildRef());

        return pdf.getId();
    }


    private Table setupUsedSearchCriteries(JSONObject jsonObject, Table table) throws JSONException {

        System.out.println("hvad er jsonObject");
        System.out.println("hvad er jsonObject");
        System.out.println("hvad er jsonObject");
        System.out.println("hvad er jsonObject");
        System.out.println(jsonObject);


        Font font = new Font("Arial", StyleTypeDefinitions.FontStyle.REGULAR, 6, Color.BLACK);

        String from = "";
        if (jsonObject.has("createdFromDate")) {
            from = jsonObject.getString("createdFromDate");
        }

        String to = "";
        if (jsonObject.has("createdToDate")) {
            to = jsonObject.getString("createdToDate");
        }

        String mainCharge = "";
        if (jsonObject.has("mainCharge")) {
            mainCharge = jsonObject.getString("mainCharge");
        }

        String mainDiagnosis = "";
        if (jsonObject.has("mainDiagnosis")) {
            mainDiagnosis = jsonObject.getString("mainDiagnosis");
        }

        String closed = "";
        if (jsonObject.has("closed")) {
            if (jsonObject.getString("closed").equals("OPEN")) {
                closed = "ikke afsluttet";
            }
            else {
                closed = "afsluttet";
            }
        }
        else  {
            closed = "alle";
        }

        String placement = "";
        if (jsonObject.has("placement")) {
            placement = jsonObject.getString("placement");
        }

        String waitingTime = "";
        if (jsonObject.has("waitingTime")) {
            waitingTime = jsonObject.getString("waitingTime");
        }

        String status = "";
        if (jsonObject.has("status")) {
            status = jsonObject.getString("status");
        }

        String psychologist = "";
        if (jsonObject.has("psychologist")) {
            psychologist = jsonObject.getString("psychologist");
        }

        String doctor = "";
        if (jsonObject.has("doctor")) {
            doctor = jsonObject.getString("doctor");
        }

        String socialworker = "";
        if (jsonObject.has("socialworker")) {
            socialworker = jsonObject.getString("socialworker");
        }

        String supervisingDoctor = "";
        if (jsonObject.has("supervisingDoctor")) {
            supervisingDoctor = jsonObject.getString("supervisingDoctor");
        }

        String declarationFromDate = "";
        if (jsonObject.has("declarationFromDate")) {
            declarationFromDate = jsonObject.getString("declarationFromDate");
        }

        String declarationToDate = "";
        if (jsonObject.has("declarationToDate")) {
            declarationToDate = jsonObject.getString("declarationToDate");
        }

        String noDeclaration = "";
        if (jsonObject.has("noDeclaration")) {
            noDeclaration = jsonObject.getString("noDeclaration");
        }

        String koen = "";
        if (jsonObject.has("koen")) {
            koen = jsonObject.getString("koen");
        }

        String cpr = "";
        if (jsonObject.has("cpr")) {
            cpr = jsonObject.getString("cpr");
        }

        String firstName = "";
        if (jsonObject.has("firstName")) {
            firstName = jsonObject.getString("firstName");
        }

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

        Row row = table.getRowByIndex(0);
        row.getCellByIndex(0).addParagraph("søgning afgivet  " + outputFormatter.format(LocalDateTime.now()));
        row.getCellByIndex(0).setHorizontalAlignment(StyleTypeDefinitions.HorizontalAlignmentType.RIGHT);

        Font fontItaliz = new Font("Arial", StyleTypeDefinitions.FontStyle.BOLDITALIC, 12, Color.BLACK);
        row.getCellByIndex(0).setFont(fontItaliz);


        if (!from.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Fra dato: " + from);
            row.getCellByIndex(0).setFont(font);
        }

        if (!to.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Til dato: " + to);
            row.getCellByIndex(0).setFont(font);
        }

        if (!mainCharge.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Hovedsigtelse: " + mainCharge);
            row.getCellByIndex(0).setFont(font);
        }

        if (!mainDiagnosis.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Hoveddiagnose: " + mainDiagnosis);
            row.getCellByIndex(0).setFont(font);
        }

        if (!placement.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Placering: " + placement);
            row.getCellByIndex(0).setFont(font);
        }

        if (!closed.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Sagsstatus: " + closed);
            row.getCellByIndex(0).setFont(font);
        }

        if (!waitingTime.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Ventetid: " + waitingTime);
            row.getCellByIndex(0).setFont(font);
        }

        if (!status.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Retslig status: " + status);
            row.getCellByIndex(0).setFont(font);
        }

        if (!psychologist.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Psykolog: " + status);
            row.getCellByIndex(0).setFont(font);
        }

        if (!doctor.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Læge: " + doctor);
            row.getCellByIndex(0).setFont(font);
        }

        if (!socialworker.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Socialrådgiver: " + socialworker);
            row.getCellByIndex(0).setFont(font);
        }

        if (!supervisingDoctor.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Tiltrædes af læge: " + supervisingDoctor);
            row.getCellByIndex(0).setFont(font);
        }

        if (!declarationFromDate.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Erklæring afgivet fra dato: " + declarationFromDate);
            row.getCellByIndex(0).setFont(font);
        }

        if (!declarationToDate.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Erklæring afgivet til dato: " + declarationToDate);
            row.getCellByIndex(0).setFont(font);
        }

        if (!noDeclaration.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Erklæring fra dato: " + noDeclaration);
            row.getCellByIndex(0).setFont(font);
        }

        if (!noDeclaration.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Afsluttet uden erklæring: " + noDeclaration);
            row.getCellByIndex(0).setFont(font);
        }

        if (!koen.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Køn: " + koen);
            row.getCellByIndex(0).setFont(font);
        }

        if (!cpr.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Cpr: " + cpr);
            row.getCellByIndex(0).setFont(font);
        }

        if (!firstName.equals("")) {
            row = table.getRowByIndex(1);
            row.getCellByIndex(0).addParagraph("Fornavn: " + firstName);
            row.getCellByIndex(0).setFont(font);
        }
        return table;
    }

    private NodeRef transform(NodeRef source) {
        NodeRef tmpFolder = siteService.getContainer("retspsyk", DatabaseModel.PROP_TMP);
        try {

            NodeRef tmp = transformBean.transformODTtoPDF(source, tmpFolder);

            // make sure to delete the node again
            nodeService.addAspect(tmp,ASPECT_TMP,null);

            return tmp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


//    private NodeRef transform(NodeRef source) {
//
//        String source_name = (String)nodeService.getProperty(source, ContentModel.PROP_NAME);
//
//        NodeRef tmpFolder = siteService.getContainer("retspsyk", DatabaseModel.PROP_TMP);
//
//        // Create new PDF
//        Map<QName, Serializable> documentLibaryProps = new HashMap<>();
////        documentLibaryProps.put(ContentModel.PROP_NAME, source_name + ".pdf");
//
//        ChildAssociationRef pdf = nodeService.createNode(tmpFolder, ContentModel.ASSOC_CONTAINS,
//                QName.createQName(ContentModel.USER_MODEL_URI, "thePDF"),
//                ContentModel.TYPE_CONTENT, documentLibaryProps);
//
//
//        ContentData contentData = (ContentData) nodeService.getProperty(source, ContentModel.PROP_CONTENT);
//        String originalMimeType = contentData.getMimetype();
//
//        ContentReader pptReader = contentService.getReader(source, ContentModel.PROP_CONTENT);
//        ContentWriter pdfWriter = contentService.getWriter(pdf.getChildRef(), ContentModel.PROP_CONTENT, true);
//
//        pdfWriter.setMimetype(MimetypeMap.MIMETYPE_PDF);
//
//        ContentTransformer pptToPdfTransformer = contentService.getTransformer(originalMimeType, MimetypeMap.MIMETYPE_PDF);
//
//        pptToPdfTransformer.transform(pptReader, pdfWriter);
//
//        return pdf.getChildRef();
//    }

}
