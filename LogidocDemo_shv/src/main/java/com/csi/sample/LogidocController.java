package com.csi.sample;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tanmoy Mandal on 12/14/2016.
 */
@Controller
public class LogidocController {
    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/connect_logidoc")
    public String connect_logidoc(Model model) throws UnsupportedEncodingException {

        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        //hash map variable for logidoc information
        Map<String , String> parameter = new HashMap<String , String> ();
        parameter.put(SessionParameter.USER, "admin");
        parameter.put(SessionParameter.PASSWORD, "admin");
        parameter.put(SessionParameter.ATOMPUB_URL, "http://localhost:8080/service/cmis");
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
//        parameter.put(SessionParameter.REPOSITORY_ID,"5");

        //collection of repository for all repositories in logidoc
        List<Repository> repositories = new ArrayList<Repository>();
        //create connection with logidoc and get all the repositories
        repositories = sessionFactory.getRepositories(parameter);

        for (Repository repo : repositories) {
            System.out.println(repo.toString());
        }
        //get repository by index
        Repository repository = repositories.get(0);
        parameter.put(SessionParameter.REPOSITORY_ID,repository.getId());
        //create session for the repository
        Session session = sessionFactory.createSession(parameter);
        System.out.println("Repository Connected: " + repository.getName() + " With ID " + repository.getId());

        //get default folder by path
        Folder root = (Folder) session.getObjectByPath("/Default");

        //get child folders of default folder
        ItemIterable<CmisObject> children = root.getChildren();
        for (CmisObject o : children) {

            System.out.println(o.getName() + " which is of type " + o.getType().getDisplayName() + " " + o.getDescription());
        }

        //hash map variable for new folder information
        Map<String, String> newFolderProps = new HashMap<String, String>();
        newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
//        newFolderProps.put(PropertyIds.PATH, "/Default/ok");
        newFolderProps.put(PropertyIds.NAME, "AddNEWFolder");
        //newFolderProps.put(PropertyIds.NAME, "ADGNewFolder");
        //create new folder under Default folder
        Folder newFolder = root.createFolder(newFolderProps);
        Folder imran=(Folder) session.getObjectByPath("/Default", "/imran");
        
        
       /* final String textFileName = "test.txt";
        System.out.println("creating a simple text file, " + textFileName);
        String mimetype = "text/plain; charset=UTF-8";
        String content = "Hi!!!";
        String filename = textFileName;
        byte[] buf = content.getBytes("UTF-8");
        ByteArrayInputStream input = new ByteArrayInputStream(buf);
        ContentStream contentStream = session.getObjectFactory().createContentStream(filename, buf.length, mimetype, input);*/
        ContentStream contentStream = null;
        try {
        	   contentStream = new ContentStreamImpl("imran.pdf", null, "file/pdf", new FileInputStream("c:/imranFile.pdf"));
        	} catch (FileNotFoundException e) {
        	   e.printStackTrace();
        	}
       
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        //properties.put(PropertyIds.NAME, filename);
        properties.put(PropertyIds.NAME, contentStream.getFileName());
       // Document doc = newFolder.createDocument(properties, contentStream, VersioningState.MAJOR);
        Document doc2 = imran.createDocument(properties, contentStream, VersioningState.MAJOR);
      //  System.out.println("Document ID: " + doc.getId());
        System.out.println("Document ID: " + doc2.getId());
       
        
      
        
        return "index";
    }
}
