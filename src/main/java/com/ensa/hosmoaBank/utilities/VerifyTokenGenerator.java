package com.ensa.hosmoaBank.utilities;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class VerifyTokenGenerator  implements IdentifierGenerator{
	@Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return generateVerificationToken();
    }

    public static String generateVerificationToken () {
        return
            UUID.randomUUID().toString()
            .concat(UUID.randomUUID().toString())
            .concat(UUID.randomUUID().toString())
            .concat(UUID.randomUUID().toString())
            .concat(UUID.randomUUID().toString())
                .replace("-", "");
    }
}
