package com.example.oidtypeconversion;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Arrays;

public class XMLUserType implements UserType {

    private final static int SQL_TYPE = Types.SQLXML;

    @Override
    public int[] sqlTypes() {
        return new int[]{SQL_TYPE};
    }

    @Override
    public Class<byte[]> returnedClass() {
        return byte[].class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Arrays.equals((byte[]) x, (byte[]) y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x != null ? x.hashCode() : 0;
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value == null ? null : ((byte[]) value).clone();
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        SQLXML xmlDoc = rs.getSQLXML(names[0]);
        return xmlDoc == null ? null : xmlDoc.getString();
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, SQL_TYPE);
        } else {

            st.setObject(index, new String((byte[]) value, StandardCharsets.UTF_8), SQL_TYPE);
        }
    }
}