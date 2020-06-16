/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import com.codename1.components.ImageViewer;
import com.codename1.components.InfiniteProgress;
import com.codename1.components.MultiButton;

import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;

import com.codename1.ui.Display;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Font;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.Slider;
import com.codename1.ui.URLImage;
import com.codename1.ui.geom.Dimension;

import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import models.Evenement;
import models.Participer;

import models.Session;
import services.Evenement_Service;
import services.Participe_Service;
import services.Vote_Service;

/**
 *
 * @author aymen
 */
public class UserInterfaceForm extends Form {

    Resources theme = UIManager.initFirstTheme("/theme");

    ArrayList<Participer> list = new ArrayList<Participer>();

    public UserInterfaceForm(Form previous) {
        super("User", BoxLayout.y());

        this.add(new InfiniteProgress());
        Display.getInstance().scheduleBackgroundTask(() -> {
            // this will take a while...

            Display.getInstance().callSerially(() -> {
                this.removeAll();
                Label logi = new Label("LES PRODUITS");

                for (Evenement c : new Evenement_Service().getAllEvents()) {

                    this.add(addItem_Event(c));
                }

                this.revalidate();
            });
        });

        this.getToolbar().addSearchCommand(e -> {
            String text = (String) e.getSource();
            if (text == null || text.length() == 0) {
                // clear search
                for (Component cmp : this.getContentPane()) {
                    cmp.setHidden(false);
                    cmp.setVisible(true);
                }
                this.getContentPane().animateLayout(150);
            } else {
                text = text.toLowerCase();
                for (Component cmp : this.getContentPane()) {
                    MultiButton mb = (MultiButton) cmp;
                    String line1 = mb.getTextLine1();
                    String line2 = mb.getTextLine2();

                    boolean show = line1 != null && line1.toLowerCase().indexOf(text) > -1
                            || line2 != null && line2.toLowerCase().indexOf(text) > -1;
                    mb.setHidden(!show);
                    mb.setVisible(show);
                }
                this.getContentPane().animateLayout(150);
            }
        }, 4);

        this.getToolbar().addCommandToSideMenu("Events", null, e -> {
            this.show();
        });

        this.getToolbar().addCommandToSideMenu("Logout", null, e -> {
            new LoginForm().showBack();
        });

    }

    public MultiButton addItem_Event(Evenement c) {
        MultiButton m = new MultiButton();
        m.setTextLine1(c.getTitre());
        m.setTextLine2(c.getLieu());

        String url = "http://localhost/projet/web/upload/" + c.getImage();

        Image imge;
        EncodedImage enc;
        enc = EncodedImage.createFromImage(theme.getImage("round.png"), false);
        imge = URLImage.createToStorage(enc, url, url);
        m.setIcon(imge);
        m.setEmblem(theme.getImage("arrow.png"));
        m.addActionListener(l
                -> {

            Form f2 = new Form("Details", BoxLayout.y());

            f2.getToolbar().addCommandToOverflowMenu("Return", null, (evt) -> {
                this.show();
            });
            Label type1 = new Label("Titre");
            Label libelle = new Label(c.getTitre());
            Label type2 = new Label("Lieu");
            Label categorie = new Label(c.getLieu());
            Label type3 = new Label("Description");
            Label site = new Label(c.getDiscription());
            Label type4 = new Label("NBR Places");
            Label Nbreplaces = new Label(String.valueOf(c.getNbreplaces()));

            Button Commenter = new Button("Avis");
            Button participer = new Button("participer");
            Button quitter = new Button("quitter");

            Container CR = new Container(new BoxLayout(BoxLayout.Y_AXIS));
            Button bou = new Button("Donner une note");
            Slider rate = createStarRankSlider();
            CR.add(FlowLayout.encloseCenter(rate));
            CR.add(FlowLayout.encloseCenter(bou));
            try {
                list = new Participe_Service().getParticipantByUser(c.getId(), Session.getCurrentSession());
                System.out.println("forms.EventUser.addItem()" + l);

            } catch (Exception ex) {
                System.out.println("ereuuuur");

            }

            Commenter.addActionListener(comm
                    -> {
                try {
                    new Commentaires(this, c.getId()).show();
                } catch (Exception ex) {
                }

            });

            participer.addActionListener(part
                    -> {
                try {
                    Participer pr = new Participer(Session.getCurrentSession(), c.getId());
                    new Participe_Service().Participer(pr);
                    new UserInterfaceForm(f2).show();
                } catch (Exception ex) {
                }

            });
            quitter.addActionListener(quit
                    -> {
                try {

                    new Participe_Service().QuiterEvenement(c.getId(), Session.getCurrentSession());

                    this.show();
                } catch (Exception ex) {
                }

            });

            String url2 = "http://localhost/projet/web/upload/" + c.getImage();
            ImageViewer imgv2;
            Image imge2;
            EncodedImage enc2;
            enc2 = EncodedImage.createFromImage(theme.getImage("round.png"), false);
            imge2 = URLImage.createToStorage(enc2, url2, url2);
            imgv2 = new ImageViewer(imge2);
            rate.setProgress(c.getNote() * 2);
            f2.getToolbar().addCommandToOverflowMenu("Stat", null, (evt)
                    -> {
                new statEvent().createPieChartForm("Abonnment", new Evenement_Service().getStat());

            });
            f2.add(imgv2).add(type1).add(libelle).add(type2).add(categorie).add(type3).add(site).add(type4).add(Nbreplaces);
            if (list.size() == 0) {
                f2.add(participer);
            } else {
                f2.add(Commenter);
                f2.add(CR);
                f2.add(quitter);
            }
            bou.addActionListener(ev -> {
                int avis = rate.getProgress() / 2;
                Vote_Service vs = new Vote_Service();
                vs.addRate(c.getId(), avis);
                System.out.println(avis);
                try {
                    new Commentaires(this, c.getId()).show();
                } catch (Exception ex) {
                }

            });

            f2.show();

        }
        );

        return m;
    }

    private void initStarRankStyle(Style s, Image star) {
        s.setBackgroundType(Style.BACKGROUND_IMAGE_TILE_BOTH);
        s.setBorder(Border.createEmpty());
        s.setBgImage(star);
        s.setBgTransparency(0);
    }

    private Slider createStarRankSlider() {
        Slider starRank = new Slider();
        starRank.setEditable(true);
        starRank.setMinValue(0);
        starRank.setMaxValue(10);
        Font fnt = Font.createTrueTypeFont("native:MainLight", "native:MainLight").
                derive(Display.getInstance().convertToPixels(5, true), Font.STYLE_PLAIN);
        Style s = new Style(0xffff33, 0, fnt, (byte) 0);
        Image fullStar = FontImage.createMaterial(FontImage.MATERIAL_STAR, s).toImage();
        s.setOpacity(100);
        s.setFgColor(0);
        Image emptyStar = FontImage.createMaterial(FontImage.MATERIAL_STAR, s).toImage();
        initStarRankStyle(starRank.getSliderEmptySelectedStyle(), emptyStar);
        initStarRankStyle(starRank.getSliderEmptyUnselectedStyle(), emptyStar);
        initStarRankStyle(starRank.getSliderFullSelectedStyle(), fullStar);
        initStarRankStyle(starRank.getSliderFullUnselectedStyle(), fullStar);
        starRank.setPreferredSize(new Dimension(fullStar.getWidth() * 5, fullStar.getHeight()));
        return starRank;
    }
}
