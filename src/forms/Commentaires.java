/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;
import com.codename1.ui.AutoCompleteTextField;
import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import models.Commentaire;
import models.Session;
import models.Vote;
import services.Commentaire_Service;
import services.User_Service;
import services.Vote_Service;
/**
 *
 * @author Rzouga
 */
public class Commentaires extends Form {
     public static int idevent ;
     Resources theme;
         public Commentaires(Form previous  , int id ) throws Exception {  
       
          super("Commentaire", BoxLayout.y());
           theme = UIManager.initFirstTheme("/theme");
           this.getToolbar().addCommandToLeftBar("return", null, evx -> {
                previous.showBack();
            });
          
          
         
          idevent = id ;
          Container cn4 = new Container(BoxLayout.y());
          TextField comment = new TextField("", "Commmenter", 20, TextArea.TEXT_CURSOR);
       
          Button Come =new Button("Commenter");
      
        
         for(models.Commentaire c:new Commentaire_Service().getAllCommentsEvent(id)){
          this.add(addItem(c));    
        }
          cn4.add(comment);
         
          cn4.add(Come);
          this.add(cn4);
          this.show();
             System.out.println("session : "+Session.getCurrentSession());
           Come.addActionListener(comme ->  
       {
            try {
                    System.out.println("session2 : "+Session.getCurrentSession());
                     System.out.println("id : "+id);
                Commentaire com = new Commentaire();
                com.setComment(comment.getText());
                com.setId(Session.getCurrentSession());
                com.setIdEvt(idevent);
                System.out.println(com);
                new Commentaire_Service().addEvent(com);
                new Commentaires(this , idevent).show();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
       });
     }
    public Container addItem(Commentaire c) throws Exception{
        Container cn1=new Container(new BorderLayout());
        Container cn2=new Container(BoxLayout.y());
        Container cn4 = new Container(BoxLayout.y());
        Button update=new Button("Update");
        Button delete=new Button("Delete");
        Button Like=new Button("Like");
        Button deslike=new Button("Deslike");
  
        Label Commentaire=new Label(c.getComment());
        Label user=new Label(new User_Service().get_User(c.getId()).getUsername());
       
        cn2.add(user);
        cn2.add(Commentaire);
        if(c.getId()==Session.getCurrentSession()){
        cn2.add(update);
        cn2.add(delete);
        }
               else{
        cn2.add(Like);
        cn2.add(deslike);
                       }
            
        cn1.add(BorderLayout.WEST, cn2);
            update.addActionListener(mod -> 
     {
         Form fmodifier = new Form("Modifier Comment", BoxLayout.y());
         Button submit = new Button("Submit");
         AutoCompleteTextField comment =  new AutoCompleteTextField(c.getComment());
         comment.setMinimumElementsShownInPopup(1);
         fmodifier.add(comment);
         fmodifier.add(submit);
         fmodifier.getToolbar().addCommandToOverflowMenu("Return", null, (evt) -> {
         this.show();
         });
         submit.addActionListener(sub ->
         {
             try {
                 Commentaire cc = new Commentaire(idevent, Session.getCurrentSession(),c.getIdComment(),comment.getText());
                 if ( new Commentaire_Service().ModifierComment(cc) == true) {
                     Dialog.show("Modifier Comment", "Commentaire modifier aves success ", "OK", null);
                 } else {
                     Dialog.show("Erreur", " Erreur de modification ", "OK", null);
                 }
                 new Commentaires(this,idevent).show();
             } catch (Exception ex) {
                 System.out.println("Erreur de modification");
             }
         }
         );
         fmodifier.show();
     } 
     );
       delete.addActionListener(sup ->  
       {
            try {
                System.out.println("Show me id commentaire"+c.getIdComment());
                new Commentaire_Service().DeleteCommentaire(c.getIdComment());
                new Commentaires(this , idevent).show();
            } catch (Exception ex) {
            }
       }
       );
         deslike.addActionListener(des ->  
       {
            try {
                System.out.println("Show me id commentaire"+c.getIdComment());
        
                new Commentaires(this , idevent).show();
            } catch (Exception ex) {
            }
       }
       );
           Like.addActionListener(sup ->  
       {
            try {
    
                new Commentaires(this , idevent).show();
            } catch (Exception ex) {
            }
       }
       );
        return cn1;
    }
}