package com.example.plan1605.service.event;

import com.example.plan1605.model.event.response.EventResponse;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class EmailJob extends QuartzJobBean {

    private final JavaMailSender mailSender;

    public EmailJob(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        EventResponse event = (EventResponse) jobDataMap.get("event");

        sendMail(jobDataMap.getString("email"), event);
    }

    private void sendMail(String email, EventResponse event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("New event");

        message.setText("Name: " + event.name() + "\n"
                                + (event.description() != null ? "Description: " + event.description() + "\n" : "")
                                + "Starts at: " + event.startsAt() + "\n"
                                + (event.endsAt() != null ? "Ends at: " + event.endsAt() + "\n" : "")
                                + (event.recurrence() != null ? "Recurring " + event.recurrence() : "")
        );

        mailSender.send(message);
    }
}
