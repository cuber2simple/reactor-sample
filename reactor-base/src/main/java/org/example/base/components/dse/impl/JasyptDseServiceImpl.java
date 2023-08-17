package org.example.base.components.dse.impl;

import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleAsymmetricConfig;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleAsymmetricStringEncryptor;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleGCMConfig;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleGCMStringEncryptor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.base.components.dse.BaseDseService;
import org.example.base.entity.DseAlgorithm;
import org.example.base.utils.JacksonUtils;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.ulisesbocchio.jasyptspringboot.util.AsymmetricCryptography.KeyFormat.PEM;


public class JasyptDseServiceImpl extends BaseDseService {

    private final StringEncryptor stringEncryptor;

    private final static String KEY_OBTENTION_ITERATIONS = "key_obtention_iterations";

    private final static String KEY_OBTENTION_ITERATIONS_DEFAULT = "1000";

    private final static String POOL_SIZE = "pool_size";

    private final static String POOL_SIZE_DEFAULT = "1";

    private final static String PROVIDER_NAME = "provider_name";

    private final static String STRING_OUTPUT_TYPE = "base64";

    private final static String SECRET_KEY_SALT = "secret_key_salt";

    private final static String PUBLIC_KEY = "public_key";

    private final static String PRIVATE_KEY = "private_key";


    public JasyptDseServiceImpl(DseAlgorithm dseAlgorithm) {
        super(dseAlgorithm);
        Config config = Config.valueOf(dseAlgorithm.getRegionId());
        Map<String, Object> PARAM = new HashMap<>();
        if (StringUtils.isNotBlank(dseAlgorithm.getAlgorithmExtra())) {
            PARAM = JacksonUtils.toMap(dseAlgorithm.getAlgorithmExtra());
        }
        stringEncryptor = switch (config) {
            case PEB -> {
                SimpleStringPBEConfig simpleStringPBEConfig = new SimpleStringPBEConfig();
                simpleStringPBEConfig.setPassword(dseAlgorithm.getSecretKey());
                simpleStringPBEConfig.setAlgorithm(dseAlgorithm.getAccessKey());
                simpleStringPBEConfig.setKeyObtentionIterations(MapUtils.getString(PARAM, KEY_OBTENTION_ITERATIONS, KEY_OBTENTION_ITERATIONS_DEFAULT));
                simpleStringPBEConfig.setPoolSize(MapUtils.getString(PARAM, POOL_SIZE, POOL_SIZE_DEFAULT));
                simpleStringPBEConfig.setProviderName(MapUtils.getString(PARAM, PROVIDER_NAME));
                simpleStringPBEConfig.setStringOutputType(STRING_OUTPUT_TYPE);
                PooledPBEStringEncryptor stringEncryptor = new PooledPBEStringEncryptor();
                stringEncryptor.setConfig(simpleStringPBEConfig);
                yield stringEncryptor;
            }
            case GCM -> {
                SimpleGCMConfig simpleGCMConfig = new SimpleGCMConfig();
                simpleGCMConfig.setSecretKeyPassword(dseAlgorithm.getSecretKey());
                simpleGCMConfig.setSecretKeyIterations(MapUtils.getInteger(PARAM, KEY_OBTENTION_ITERATIONS, Integer.valueOf(KEY_OBTENTION_ITERATIONS_DEFAULT)));
                simpleGCMConfig.setSecretKeySalt(MapUtils.getString(PARAM, SECRET_KEY_SALT));
                simpleGCMConfig.setSecretKeyAlgorithm(dseAlgorithm.getAccessKey());
                yield new SimpleGCMStringEncryptor(simpleGCMConfig);
            }
            case ASYMMETRIC -> {
                SimpleAsymmetricConfig simpleAsymmetricConfig = new SimpleAsymmetricConfig();
                simpleAsymmetricConfig.setKeyFormat(PEM);
                simpleAsymmetricConfig.setPublicKey(MapUtils.getString(PARAM, PUBLIC_KEY));
                simpleAsymmetricConfig.setPrivateKey(MapUtils.getString(PARAM, PRIVATE_KEY));
                yield new SimpleAsymmetricStringEncryptor(simpleAsymmetricConfig);
            }
        };

    }


    @Override
    public Mono<String> encode(byte[] originBytes) {
        return Mono.defer(() -> Mono.create(stringMonoSink -> {
            try {
                stringMonoSink.success(stringEncryptor.encrypt(new String(originBytes, StandardCharsets.UTF_8)));
            } catch (Exception e) {
                stringMonoSink.error(e);
            }
        }));
    }

    @Override
    public Mono<String> encode(String origin) {
        return Mono.defer(() -> Mono.create(stringMonoSink -> {
            try {
                stringMonoSink.success(stringEncryptor.encrypt(origin));
            } catch (Exception e) {
                stringMonoSink.error(e);
            }
        }));
    }

    @Override
    public Mono<String> decode(String dseInfo) {
        return Mono.defer(() -> Mono.create(stringMonoSink -> {
            try {
                stringMonoSink.success(stringEncryptor.decrypt(dseInfo));
            } catch (Exception e) {
                stringMonoSink.error(e);
            }
        }));
    }

    enum Config {
        PEB,
        GCM,
        ASYMMETRIC,
    }

    public static void main(String[] args) throws InterruptedException {
        DseAlgorithm dseAlgorithm = new DseAlgorithm();
        dseAlgorithm.setRegionId("PEB");
        dseAlgorithm.setAccessKey("PBEWITHSHA1ANDRC2_40");
        dseAlgorithm.setSecretKey("chupacabras");
        dseAlgorithm.setIdx((short) 1);
        JasyptDseServiceImpl jasyptDseService = new JasyptDseServiceImpl(dseAlgorithm);

        DseAlgorithm dseAlgorithm1 = new DseAlgorithm();
        dseAlgorithm1.setRegionId("GCM");

        Map<String, Object> param = new HashMap<>();
        param.put("secret_key_salt", "HrqoFr44GtkAhhYN+jP8Ag==");


        dseAlgorithm1.setAccessKey("PBKDF2WithHmacSHA256");
        dseAlgorithm1.setSecretKey("chupacabras");
//        dseAlgorithm1.setAlgorithmExtra(JacksonUtils.toJson(param));
        System.out.println(dseAlgorithm1.getAlgorithmExtra());
        dseAlgorithm1.setIdx((short) 1);
        JasyptDseServiceImpl jasyptDseService1 = new JasyptDseServiceImpl(dseAlgorithm1);
        DseAlgorithm dseAlgorithm2 = new DseAlgorithm();
        dseAlgorithm2.setRegionId("ASYMMETRIC");
        param.put("public_key", "-----BEGIN PUBLIC KEY-----\n" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDKi/d47jY+RrlaIM4dA9LnefVC\n" +
                "0joNQk1RF1+WyriKUFe4EBtJK8uVWf0S4DCGM5tswqSXwLpzg0IK9Qq2OFmfk6mX\n" +
                "uoeFw//N2+X8wHdtvsBks1pw5xfMqvkEOyNtxak+52dHpqbqYtKYtcbMGTVwixPn\n" +
                "oBtLJGjKIWAuXq3ZkQIDAQAB\n" +
                "-----END PUBLIC KEY-----");
        param.put("private_key", "-----BEGIN PRIVATE KEY-----\n" +
                "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMqL93juNj5GuVog\n" +
                "zh0D0ud59ULSOg1CTVEXX5bKuIpQV7gQG0kry5VZ/RLgMIYzm2zCpJfAunODQgr1\n" +
                "CrY4WZ+TqZe6h4XD/83b5fzAd22+wGSzWnDnF8yq+QQ7I23FqT7nZ0empupi0pi1\n" +
                "xswZNXCLE+egG0skaMohYC5erdmRAgMBAAECgYAPLxj5PtpgJsskX4s0D6JS6gih\n" +
                "3sKtVcgYCSmIU2AsNkOtL7/r51WWt61KKfZZSSsgpyLRoYgYusFLsvz/lg/ZzW+6\n" +
                "pqxbQxoKT2xALqckEMp0K+lyV1wd8yw/T3lvURiW1K3svfZDSioVpqEmbc25dfh/\n" +
                "+ZuEmhnUoeMOk9wmQQJBAPNLcx68YBaYpn8aQj0DCFIihO3fcgc9Agaz3iYAK9AH\n" +
                "5AqOObZ25QYIe/NZwaDAdDlFo7Ep4jIXuLltf380ugkCQQDVH8RLZIAcUn+AuF2P\n" +
                "bCA67pba6oryfCZVlCtcHsmQwbeL4wl2hK5fysyiZAX95OWd74MiFgeu65QAAuqK\n" +
                "FaVJAkADomXm2KOK0t7x2R+SL8BpEbDwqLzYVNX56afw42HEKsoCSlucWSxUqb3I\n" +
                "Rdf5ocZasKqgU+LYIriUtIkKCmyZAkEAkIibmbmNfCeimovtv19HuE+n9MzM8Eer\n" +
                "w6vZf/7NGaOWM4MEHuE6VHJ3NsX4nkfGRMZyuwIPsdvHof5YSs8FKQJBAJLOE28U\n" +
                "6Ps8LHj11LMK4SMho+oAyiQvP7PHQ+RpRiJUmxl7VJnEKicTtZNRV88zWMPNnaTV\n" +
                "4GWobql4cG9fpXk=\n" +
                "-----END PRIVATE KEY-----");
//        dseAlgorithm2.setAlgorithmExtra(JacksonUtils.toJson(param));
        dseAlgorithm2.setIdx((short) 1);
        System.out.println(dseAlgorithm2.getAlgorithmExtra());
        JasyptDseServiceImpl jasyptDseService2 = new JasyptDseServiceImpl(dseAlgorithm2);


        String encode = "123123424234324532";
        String encrypt = jasyptDseService1.stringEncryptor.encrypt(encode);
        String decrypt = jasyptDseService1.stringEncryptor.decrypt(encrypt);
        System.out.println(decrypt);
    }
}
