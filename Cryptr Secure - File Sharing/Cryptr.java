import javax.crypto.*;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Random;
import java.nio.file.Paths;
import java.security.*;
import java.nio.file.Files;


/*
 *               Cryptr
 *
 * Cryptr is a java encryption toolset
 * that can be used to encrypt/decrypt files
 * and keys locally, allowing for files to be
 * shared securely over the world wide web
 *
 * Cryptr provides the following functions:
 *   1. Generating a secret key
 *   2. Encrypting a file with a secret key
 *   3. Decrypting a file with a secret key
 *   4. Encrypting a secret key with a public key
 *   5. Decrypting a secret key with a private key
 *
 */

public class Cryptr {


    /**
     * Generates an 128-bit AES secret key and writes it to a file
     *
     * @param  secKeyFile    name of file to store secret key
     */
    static void generateKey(String secKeyFile) throws Exception{

        /*   FILL HERE   */
        //Steps -
        //byte[] temp = new btye[];
        byte[] initialVector = new byte[128/8];
        //Sudo randomization
        Random rand = new Random();
        //Makes next byte random
        rand.nextBytes(initialVector);
        //uses iv to decrpyt to help genereate a key
        IvParameterSpec vectorS = new IvParameterSpec(initialVector);
        //System.out.println(vectorS);
        KeyGenerator x = KeyGenerator.getInstance("AES");

        //System.out.println(x);

        //System.out.println(x);
        SecretKey s = x.generateKey();
        //System.out.println(s);

        String keyToFile =secKeyFile;

        try (FileOutputStream out = new FileOutputStream(keyToFile)) {
            byte[] temp = s.getEncoded();
            out.write(temp);
            //System.out.println("Working1");
            out.write(initialVector);
            //System.out.println("Working2");
        }
    }


    /**
     * Extracts secret key from a file, generates an
     * initialization vector, uses them to encrypt the original
     * file, and writes an encrypted file containing the initialization
     * vector followed by the encrypted file data
     *
     * @param  originalFile    name of file to encrypt
     * @param  secKeyFile      name of file storing secret key
     * @param  encryptedFile   name of file to write iv and encrypted file data
     */
    static void encryptFile(String originalFile, String secKeyFile, String encryptedFile) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {

        /*   FILL HERE   */
        String reF = originalFile;
        String oF = encryptedFile;
        String KTF = secKeyFile;
        //System.out.println(KTF);


        byte[] k =  Files.readAllBytes(Paths.get(KTF));
        //System.out.println(k);
        SecretKeySpec sks = new SecretKeySpec(k, "AES");
        byte[] initialVector = new byte[128/8];

        Random num =new Random();
        //System.out.println(num);
        num.nextBytes(initialVector);
        //System.out.println(num.nextBytes(initialVector));

        IvParameterSpec vectorS = new IvParameterSpec(initialVector);
        //System.out.println("Working3");


        Cipher stuff = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //System.out.println("Working4");

        stuff.init(Cipher.ENCRYPT_MODE, sks, vectorS);
        //System.out.println("Working5");

        pInfo(stuff, reF, oF, vectorS);
        //System.out.println("Working6");
    }

    //Used to process the infomation of th file using the key, stuff continauly read the lines and convert it
    static private void pInfo(Cipher stuff, String reF, String oF, IvParameterSpec vectorS) throws javax.crypto.IllegalBlockSizeException, javax.crypto.BadPaddingException, java.io.IOException {
        byte[] temper = vectorS.getIV();
        try (FileInputStream use = new FileInputStream(reF);
             FileOutputStream send = new FileOutputStream(oF)){
            send.write(temper);
            int number = 1024;
            int i;
            int t = 0;
            int error = -1;
            //System.out.println("IN this method");

            byte[] holder = new byte[number];

            //Reads the input stream
            while ((i = use.read(holder)) != error){

                //encrpyts
                byte[] holder1 = stuff.update(holder, t, i);

                //reads out to the file
                if ( holder1 != null ){
                    //System.out.println("Not null!");
                    //System.out.println("Write");
                    send.write(holder1);
                }
                /*else{
                     //System.out.println("null!");
                }*/
            }

            //finalizes the encryption process
            byte[] final1 = stuff.doFinal();
            if (final1 != null){
                //System.out.println("Not null!");
                //System.out.println("Write");
                send.write(final1);
            }
            /*else{
                //System.out.println("null!");
            }*/
        }
    }

    /**
     * Extracts the secret key from a file, extracts the initialization vector
     * from the beginning of the encrypted file, uses both secret key and
     * initialization vector to decrypt the encrypted file data, and writes it to
     * an output file
     *
     * @param  encryptedFile    name of file storing iv and encrypted data
     * @param  secKeyFile       name of file storing secret key
     * @param  outputFile       name of file to write decrypted data to
     */
    static void decryptFile(String encryptedFile, String secKeyFile, String outputFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        //System.out.println(secKeyFile)
        byte[] k = Files.readAllBytes(Paths.get(secKeyFile));

        int tempp = 128/8;


        SecretKeySpec sk = new SecretKeySpec(k, "AES");
        //System.out.println(sk);

        FileInputStream file = new FileInputStream(encryptedFile);

        byte[] initialVector = new byte[tempp];

        file.read(initialVector);
        //System.out.println("working");

        IvParameterSpec vectorS = new IvParameterSpec(initialVector);
        //System.out.println("working");
        Cipher stuff = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //System.out.println("working");
        //System.out.println(stuff);
        stuff.init(Cipher.DECRYPT_MODE, sk, vectorS);

        //try
        try (FileOutputStream send = new FileOutputStream(outputFile)){
            pEnFile(stuff, file, send);
        }
        catch (BadPaddingException e) {
            e.printStackTrace();
        }
        catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    //encrpytion of the key
    static private void pEnFile(Cipher stuff,InputStream push,OutputStream send) throws javax.crypto.IllegalBlockSizeException, javax.crypto.BadPaddingException, java.io.IOException {
        int number1 = 1024;
        int error = -1;
        int emp = 0;
        byte[] temp1 = new byte[number1];
        int i;
        while ((i = push.read(temp1)) != error) {
            //System.out.println("working");
            //System.out.println(i);
            byte[] temp2 = stuff.update(temp1, emp, i);
            if (temp2 != null) {
                //System.out.println("not null!");
                send.write(temp2);
            }
        }
        byte[] temp2 = stuff.doFinal();


        if ( temp2 != null ) {
            //System.out.println("not null!");
            send.write(temp2);
        }
    }

    /**
     * Extracts secret key from a file, encrypts a secret key file using
     * a public Key (*.der) and writes the encrypted secret key to a file
     *
     * @param  secKeyFile    name of file holding secret key
     * @param  pubKeyFile    name of public key file for encryption
     * @param  encKeyFile    name of file to write encrypted secret key
     */
    static void encryptKey(String secKeyFile, String pubKeyFile, String encKeyFile) throws InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException {

        //read bytes
        byte[] tempHold = Files.readAllBytes(Paths.get(pubKeyFile));

        X509EncodedKeySpec keysspec = new X509EncodedKeySpec(tempHold);
        //System.out.println(keysspec);
        //System.out.println("lets gooo");
        KeyFactory x = KeyFactory.getInstance("RSA");
        //System.out.println(x);
        //System.out.println("Letssss gooo");
        PublicKey bar = x.generatePublic(keysspec);
        //System.out.println("bar");
        Cipher stuff = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        //System.out.println(stuff);
        stuff.init(Cipher.ENCRYPT_MODE, bar);

        //try
        try (FileInputStream get = new FileInputStream(secKeyFile);
             FileOutputStream send = new FileOutputStream(encKeyFile)) {
            //System.out.println("work pl");
            pEnFile(stuff, get, send);
        }
    }


    /**
     * Decrypts an encrypted secret key file using a private Key (*.der)
     * and writes the decrypted secret key to a file
     *
     * @param  encKeyFile       name of file storing encrypted secret key
     * @param  privKeyFile      name of private key file for decryption
     * @param  secKeyFile       name of file to write decrypted secret key
     */
    static void decryptKey(String encKeyFile, String pvtKeyFile, String secKeyFile)throws InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException  {
        //read bytes
        byte[] tempHold = Files.readAllBytes(Paths.get(pvtKeyFile));

        //System.out.println("lets gooo");
        PKCS8EncodedKeySpec keySpa = new PKCS8EncodedKeySpec(tempHold);
        //System.out.println("lets gooo");
        //System.out.println(keySpa);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        //System.out.println("lets gooo");
        //System.out.println(fact);
        PrivateKey pri = fact.generatePrivate(keySpa);
        //System.out.println("lets gooo");
        //System.out.println(pri);
        Cipher stuff = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        //System.out.println("lets gooo");
        //System.out.println(stuff);
        stuff.init(Cipher.DECRYPT_MODE, pri);
        //System.out.println("Done");
        try (FileInputStream get = new FileInputStream(encKeyFile);
             FileOutputStream send = new FileOutputStream(secKeyFile)) {
            //System.out.println("Work plz");
            pEnFile(stuff, get, send);
        }
    }


    /**
     * Main Program Runner
     */
    public static void main(String[] args) throws Exception{

        String func;

        if(args.length < 1) {
            func = "";
        } else {
            func = args[0];
        }

        switch(func)
        {
            case "generatekey":
                if(args.length != 2) {
                    System.out.println("Invalid Arguments.");
                    System.out.println("Usage: Cryptr generatekey <key output file>");
                    break;
                }
                System.out.println("Generating secret key and writing it to " + args[1]);
                generateKey(args[1]);
                break;
            case "encryptfile":
                if(args.length != 4) {
                    System.out.println("Invalid Arguments.");
                    System.out.println("Usage: Cryptr encryptfile <file to encrypt> <secret key file> <encrypted output file>");
                    break;
                }
                System.out.println("Encrypting " + args[1] + " with key " + args[2] + " to "  + args[3]);
                encryptFile(args[1], args[2], args[3]);
                break;
            case "decryptfile":
                if(args.length != 4) {
                    System.out.println("Invalid Arguments.");
                    System.out.println("Usage: Cryptr decryptfile <file to decrypt> <secret key file> <decrypted output file>");
                    break;
                }
                System.out.println("Decrypting " + args[1] + " with key " + args[2] + " to " + args[3]);
                decryptFile(args[1], args[2], args[3]);
                break;
            case "encryptkey":
                if(args.length != 4) {
                    System.out.println("Invalid Arguments.");
                    System.out.println("Usage: Cryptr encryptkey <key to encrypt> <public key to encrypt with> <encrypted key file>");
                    break;
                }
                System.out.println("Encrypting key file " + args[1] + " with public key file " + args[2] + " to " + args[3]);
                encryptKey(args[1], args[2], args[3]);
                break;
            case "decryptkey":
                if(args.length != 4) {
                    System.out.println("Invalid Arguments.");
                    System.out.println("Usage: Cryptr decryptkey <key to decrypt> <private key to decrypt with> <decrypted key file>");
                    break;
                }
                System.out.println("Decrypting key file " + args[1] + " with private key file " + args[2] + " to " + args[3]);
                decryptKey(args[1], args[2], args[3]);
                break;
            default:
                System.out.println("Invalid Arguments.");
                System.out.println("Usage:");
                System.out.println("  Cryptr generatekey <key output file>");
                System.out.println("  Cryptr encryptfile <file to encrypt> <secret key file> <encrypted output file>");
                System.out.println("  Cryptr decryptfile <file to decrypt> <secret key file> <decrypted output file>");
                System.out.println("  Cryptr encryptkey <key to encrypt> <public key to encrypt with> <encrypted key file> ");
                System.out.println("  Cryptr decryptkey <key to decrypt> <private key to decrypt with> <decrypted key file>");
        }

        System.exit(0);

    }

}