package healthscape.com.healthscape.fabric.service;

import healthscape.com.healthscape.fabric.model.FabricEnrollment;
import healthscape.com.healthscape.fabric.model.FabricUser;
import healthscape.com.healthscape.fabric.util.HLFRegistrationException;
import healthscape.com.healthscape.fabric.util.WalletUtil;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.EncryptionConfig;
import healthscape.com.healthscape.util.HashUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.Attribute;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Transactional
public class FabricUserService {

    private final WalletUtil walletUtil;
    private final UserService userService;
    private final EncryptionConfig encryptionConfig;

    private static HFCAClient createCaClient() throws MalformedURLException, CryptoException, InvalidArgumentException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Properties props = new Properties();
        props.put("pemFile", Config.ORG_CA_CERT_PATH.toString());
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = HFCAClient.createNewInstance(Config.CA_URL, props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);
        return caClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            enrollAdmin();
            List<AppUser> allUsers = userService.getUsers();
            for(AppUser user: allUsers){
                if(!user.getRole().getName().equals("ROLE_ADMIN")){
                    registerUser(user);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void enrollAdmin() throws Exception {

        FabricUser fabricAdmin = findAdmin();
        if (fabricAdmin != null) {
            System.out.println("An identity for the admin user \"admin\" already exists in the wallet");
            return;
        }

        HFCAClient caClient = createCaClient();
        userService.registerAdmin();
        String encryptedId = encryptionConfig.encryptDefaultData(Config.ADMIN_ID);
        final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
        enrollmentRequestTLS.addHost(Config.CA_HOST);
        enrollmentRequestTLS.setProfile("tls");
        Enrollment enrollment = caClient.enroll(Config.ADMIN_IDENTITY_ID, Config.ADMIN_PASSWORD, enrollmentRequestTLS);
        Identity user = Identities.newX509Identity("Org1MSP", enrollment);
        walletUtil.putIdentity(encryptedId, user);
        System.out.println("Successfully enrolled user \"admin\" and imported it into the wallet");
    }

    public void registerUser(AppUser appUser) throws Exception {
        String id = HashUtil.hashData(appUser.getId().toString());
        String encryptedId = this.encryptionConfig.defaultEncryptionUtil().encryptIfNotAlready(appUser.getId().toString());
        if (walletUtil.doesExistById(id)) {
            throw new HLFRegistrationException(String.format("An identity for the user %s already exists in the wallet", appUser.getId()));
        }

        FabricUser fabricAdmin = findAdmin();
        if (fabricAdmin == null) {
            throw new HLFRegistrationException("\"Admin\" needs to be enrolled and added to the wallet first");
        }

        HFCAClient caClient = createCaClient();
        RegistrationRequest registrationRequest = new RegistrationRequest(id);
        registrationRequest.setAffiliation("org1.department1");
        registrationRequest.setEnrollmentID(id);
        registrationRequest.addAttribute(new Attribute("role", appUser.getRole().getName()));
        registrationRequest.addAttribute(new Attribute("name", appUser.getName() + " " + appUser.getSurname()));
        String enrollmentSecret = caClient.register(registrationRequest, fabricAdmin);
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
        enrollmentRequest.addAttrReq("role");
        enrollmentRequest.addAttrReq("name");
        Enrollment enrollment = caClient.enroll(id, enrollmentSecret, enrollmentRequest);
        Identity user = Identities.newX509Identity("Org1MSP", enrollment);
        walletUtil.putIdentity(encryptedId, user);
        System.out.printf("Successfully enrolled user %s and imported it into the wallet \n", id);
    }

    public void unregisterUser(AppUser appUser) {
        String id = this.encryptionConfig.defaultEncryptionUtil().encryptIfNotAlready(appUser.getId().toString());
        try {
            walletUtil.deleteIdentity(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private FabricUser findAdmin() throws IOException {
        AppUser admin = userService.getUserByRole("ROLE_ADMIN");
        if (admin == null || !walletUtil.doesAdminExist()) {
            return null;
        }

        X509Identity adminIdentity = walletUtil.getAdminIdentity();
        Enrollment adminEnrolment = new FabricEnrollment(adminIdentity.getPrivateKey(), Identities.toPemString(adminIdentity.getCertificate()));
        return new FabricUser("admin", null, null, "org1.department1", adminEnrolment, "Org1MSP");
    }

}
