package ftp.client.FTPClient.service;

/**
 * Created by viscoze on 2/10/17.
 */

public interface FTPReply {

    /**
     * Connects to ftp server
     * @param reply code status from server
     * @returns if (status code is in positive preliminary range else false)
     */
    static boolean isPositivePreliminary(int reply) {
        return (reply >= 100 && reply < 200);
    }

    /**
     * Connects to ftp server
     * @param reply code status from server
     * @returns if (status code is in positive completion range else false)
     */
    static boolean isPositiveCompletion(int reply) {
        return (reply >= 200 && reply < 300);
    }

    /**
     * Connects to ftp server
     * @param reply code status from server
     * @returns if (status code is in positive intermediate range else false)
     */
    static boolean isPositiveIntermediate(int reply) {
        return (reply >= 300 && reply < 400);
    }

    /**
     * Connects to ftp server
     * @param reply code status from server
     * @returns if (status code is in negative transient range else false)
     */
    static boolean isNegativeTransient(int reply) {
        return (reply >= 400 && reply < 500);
    }

    /**
     * Connects to ftp server
     * @param reply code status from server
     * @returns if (status code is in negative permanent range else false)
     */
    static boolean isNegativePermanent(int reply) {
        return (reply >= 500 && reply < 600);
    }

    /**
     * Connects to ftp server
     * @param reply code status from server
     * @returns if (status code is in protected reply code else false)
     */
    static boolean isProtectedReplyCode(int reply) {
       return (reply >= 600 && reply < 700);
    }
}