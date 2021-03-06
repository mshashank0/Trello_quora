package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Create new user in database.
     *
     * @param userEntity : userEntity body
     * @return User details
     */
    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    /**
     * Method to get user by name
     *
     * @param userName : Fetch user via UserName
     * @return user details
     */
    public UserEntity getUserByUserName(final String userName) {
        try {
            return entityManager.createNamedQuery("userByUserName", UserEntity.class).setParameter("userName", userName).getSingleResult();
        }catch(NoResultException nre){
            return  null;
        }
    }

    /**
     * Method to get user by email
     *
     * @param email : Fetch user via email
     * @return user details
     */
    public UserEntity getUserByEmail(final String email) {
        try{
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        }catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Fetch a single user by given id from the DB.
     *
     * @param uuid Id of the user whose information is to be fetched.
     * @return User details if exist in the DB else null.
     */
    public UserEntity getUserById(final String uuid) {
        try{
            return entityManager.createNamedQuery("userByUserId", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        }catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to update user in database
     *
     * @param updatedUserEntity : UserEntity body
     * @return updated response
     */
    public void updateUserEntity(final UserEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }

    /**
     * Method to delete user by id
     *
     * @param uuid : username which you want to delete
     * @return deleted response
     */
    public UserEntity deleteUser(final String uuid) {
        UserEntity deleteUser = getUserById(uuid);
        if (deleteUser != null) {
            this.entityManager.remove(deleteUser);
        }
        return deleteUser;
    }
}