package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterTemplate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
@Service
@Transactional
public class NewsletterTemplateServiceImpl extends CRUDServiceImpl<NewsletterTemplate> implements NewsletterTemplateService {

    @Autowired
    private SessionFactory sessionFactory;
    
    private final static Logger logger = LoggerFactory.getLogger(NewsletterTemplateServiceImpl.class);
    
   
    
}
