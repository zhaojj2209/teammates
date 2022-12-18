package teammates.ui.servlets;

import org.hibernate.SessionFactory;
import teammates.common.util.HibernateUtil;
import teammates.common.util.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class HibernateSessionRequestFilter implements Filter {

    private static final Logger log = Logger.getLogger();
    private SessionFactory sessionFactory;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {
            sessionFactory.getCurrentSession().beginTransaction();

            chain.doFilter(request, response);

            sessionFactory.getCurrentSession().getTransaction().commit();
            sessionFactory.getCurrentSession().close();
        } catch (Throwable ex) {
            try {
                if (sessionFactory.getCurrentSession().getTransaction().isActive()) {
                    sessionFactory.getCurrentSession().getTransaction().rollback();
                }
            } catch (Throwable rbEx) {
                log.severe("Could not rollback transaction", rbEx);
            }

            throw new ServletException(ex);
        }
    }

    @Override
    public void destroy() {
        // nothing to do
    }
}
