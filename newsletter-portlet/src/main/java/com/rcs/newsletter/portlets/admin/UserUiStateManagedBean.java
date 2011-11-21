package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journalcontent.util.JournalContentUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author juan
 */
@Named
@Scope("session")
public class UserUiStateManagedBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public static final int LISTS_TAB_INDEX = 0;
    public static final int SUBSCRIBERS_TAB_INDEX = 1;
    public static final int MAILING_TAB_INDEX = 2;
    public static final int ARCHIVE_TAB_INDEX = 3;
    
    @Value("${newsletter.articles.type}")
    private String newsletterArticleType;
    
    private int adminActiveTabIndex;
    private static final Logger logger = LoggerFactory.getLogger(UserUiStateManagedBean.class);
    
    //global lists
    List<JournalArticle> journalArticles;

    @PostConstruct
    public void init() {
        adminActiveTabIndex = LISTS_TAB_INDEX;
        refresh();
    }

    public int getAdminActiveTabIndex() {
        return adminActiveTabIndex;
    }

    public void setAdminActiveTabIndex(int adminActiveTabIndex) {
        this.adminActiveTabIndex = adminActiveTabIndex;
    }

    public void onTabsUpdated(TabChangeEvent event) {
        //DUMMY METHOD TO MAKE THE MAGIC HAPPEN
        System.out.println(adminActiveTabIndex);
    }

    /**
     * Obtain an article by his id
     * @param articleId
     * @return 
     */
    public JournalArticle getJournalArticleByArticleId(long articleId) {
        JournalArticle result = null;

        try {
            result = JournalArticleLocalServiceUtil.getArticle(articleId);
        } catch (Exception e) {
            logger.error("Error while trying to get the journal article with id: " + articleId, e);
        }

        return result;
    }

    public List<JournalArticle> getJournalArticles() {
        return journalArticles;
    }

    /**
     * Obtain the title of the article by his id
     * @param articleId
     * @return 
     */
    public String getTitleByArticleId(long articleId) {
        String result = "";
        JournalArticle journalArticle = getJournalArticleByArticleId(articleId);

        if (journalArticle != null) {
            result = journalArticle.getTitle();
        }

        return result;
    }

    /**
     * Obtain the content of the article
     * @param journalArticle
     * @return 
     */
    public String getContent(JournalArticle journalArticle) {
        String result = null;
        ThemeDisplay themeDisplay = getThemeDisplay();
        result = JournalContentUtil.getContent(journalArticle.getGroupId(),
                journalArticle.getArticleId(),
                journalArticle.getTemplateId(),
                Constants.PRINT,
                themeDisplay.getLanguageId(),
                themeDisplay);
        return result;
    }

    public ThemeDisplay getThemeDisplay() {
        ThemeDisplay result = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();

        Map requestMap = facesContext.getExternalContext().getRequestMap();
        result = (ThemeDisplay) requestMap.get(WebKeys.THEME_DISPLAY);

        return result;
    }

    /**
     * Method that filter the articles by a specified type
     * @param type
     * @return 
     */
    public List<JournalArticle> findArticlesByType(String type) {
        HashMap<String, JournalArticle> result = new HashMap<String, JournalArticle>();
        try {
            List<JournalArticle> allJournalArticles = JournalArticleLocalServiceUtil.getArticles();

            for (JournalArticle article : allJournalArticles) {
                //We only put the last version of the article
                if (!result.containsKey(article.getArticleId())
                        && article.getType().equals(type)) {
                    JournalArticle lastArticle = JournalArticleLocalServiceUtil.getLatestArticle(
                            article.getGroupId(),
                            article.getArticleId());
                    result.put(lastArticle.getArticleId(), lastArticle);
                }
            }
        } catch (SystemException ex) {
            logger.warn("Could not filter the articles by this category", ex);
        } catch (PortalException ex) {
            logger.warn("Could not filter the articles by this category", ex);
        }

        return new ArrayList<JournalArticle>(result.values());
    }

    public void refresh() {
        try {
            journalArticles = findArticlesByType(newsletterArticleType);
        } catch (Exception ex) {
            logger.error("Error while trying to get the list of journal articles", ex);
        }
    }

    public String redirectAdmin() {
        return "admin?faces-redirect=true";
    }
}
