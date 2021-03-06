package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    /**
     * SignUp method for users and add salt, encryption to password
     *
     * @throws SignUpRestrictedException : throw exception if user already exists
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signUp(final UserEntity userEntity) throws SignUpRestrictedException {

        if(isUserNameInUse(userEntity.getUserName())) {
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }

        if(isEmailInUse(userEntity.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        // Encrypted password and salt assigned to the user that is being created.
        final String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);
    }

    /**
     * the signin user method
     *
     * @param username : Username that you want to signin
     * @param password : Password of user
     * @throws AuthenticationFailedException : If user not found or invalid password
     * @return UserAuthEntity access-token and singin response.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signIn(final String username, final String password) throws AuthenticationFailedException {
        final UserEntity userEntity = userDao.getUserByUserName(username);

        if(userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());

        if(!encryptedPassword.equals(userEntity.getPassword())) {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
        final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
        final UserAuthEntity userAuthEntity = new UserAuthEntity();
        userAuthEntity.setUuid(UUID.randomUUID().toString());
        userAuthEntity.setUserEntity(userEntity);
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(8);
        userAuthEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
        userAuthEntity.setLoginAt(now);
        userAuthEntity.setExpiresAt(expiresAt);

        userAuthDao.createAuthToken(userAuthEntity);
        userDao.updateUserEntity(userEntity);
        return userAuthEntity;
    }

    /**
     * The signout method
     *
     * @param accessToken : required to signout the user
     * @throws SignOutRestrictedException : if the access-token is not found in the DB.
     * @return UserEntity : that user is signed out.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signOut(final String accessToken) throws SignOutRestrictedException {
        final UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);

        if(userAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }

        userAuthEntity.setLogoutAt(ZonedDateTime.now());
        userAuthDao.updateUserAuth(userAuthEntity);
        return userAuthEntity.getUserEntity();
    }

    // To check if the username exist in the database
    private boolean isUserNameInUse(final String userName) {
        return userDao.getUserByUserName(userName) != null;
    }

    // To check if the email exist in the database
    private boolean isEmailInUse(final String email) {
        return userDao.getUserByEmail(email) != null;
    }
}
