package com.ensa.hosmoaBank.models.logs;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.persistence.Entity;

import com.ensa.hosmoaBank.models.Admin;
import com.ensa.hosmoaBank.models.Agent;
import com.ensa.hosmoaBank.models.Client;

import javax.persistence.*;

@Entity
public class Logs implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String path;

    @OneToOne
    private Admin admin;

    @OneToOne
    private Agent agent;

    @OneToOne
    private Client client;

//    protected Logs(String name, String resourceBundleName) {
//        super(name, resourceBundleName);
//    }
//
//    protected Logs (String name) {
//        super(name);
//    }
//
//    @Override
//    public void info(Object message) {
//        super.info(message);
//        db.save(message);
//    }


        public void log(){
        Logger logger = Logger.getLogger("IdLog "+id);
        FileHandler fh;
        try {

            fh = new FileHandler(path);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
