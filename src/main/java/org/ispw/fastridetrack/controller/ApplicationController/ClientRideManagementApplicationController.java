package org.ispw.fastridetrack.controller.ApplicationController;

import jakarta.mail.MessagingException;
import org.ispw.fastridetrack.bean.EmailBean;
import org.ispw.fastridetrack.bean.TaxiRideConfirmationBean;
import org.ispw.fastridetrack.dao.TaxiRideDAO;
import org.ispw.fastridetrack.model.TaxiRideConfirmation;
import org.ispw.fastridetrack.model.Session.SessionManager;
import org.ispw.fastridetrack.dao.Adapter.EmailService;
import org.ispw.fastridetrack.dao.Adapter.GmailAdapter;

public class ClientRideManagementApplicationController {

    private final TaxiRideDAO taxiRideDAO;
    private final EmailService emailService;

    public ClientRideManagementApplicationController() {
        SessionManager session = SessionManager.getInstance();
        this.taxiRideDAO = session.getTaxiRideDAO();
        this.emailService = new GmailAdapter();
    }


    //Confermo una corsa e invio una notifica al driver via email.
    public void confirmRideAndNotify(TaxiRideConfirmationBean bean, EmailBean email) throws MessagingException {
        bean.markPending();

        TaxiRideConfirmation model = bean.toModel();

        // Salvataggio della corsa
        if (!taxiRideDAO.exists(model.getRideID())) {
            taxiRideDAO.save(model);
        } else {
            taxiRideDAO.update(model);
        }

        // Invio notifica al driver
        emailService.sendEmail(email.getRecipient(), email.getSubject(), email.getBody());
    }
}


