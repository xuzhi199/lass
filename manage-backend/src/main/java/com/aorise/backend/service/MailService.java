package com.aorise.backend.service;

import com.aorise.model.common.Page;
import com.aorise.model.mail.Mail;

import java.util.Map;

public interface MailService {

    void saveMail(Mail mail);

    void updateMail(Mail mail);

    void sendMail(Mail mail);

    Mail findById(Long id);

    Page<Mail> findMails(Map<String, Object> params);
}
