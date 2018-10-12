/*
 * ChallengeCurves.java
 */




package de.rub.nds.ec;




/**
 * This class holds the curves from certicom ecc challenge as static members.
 * These can be used for testing purposes and are listed here.
 * 
 * <code><br /><br />
 *    ======= ECCp-79 =======<br />
 *    p = 62CE 5177412A CA899CF5<br />
 *    a = 39C9 5E6DDDB1 BC45733C<br />
 *    b = 1F16 D880E89D 5A1C0ED1<br />
 *    P_x = 315D 4B201C20 8475057D<br />
 *    P_y = 035F 3DF5AB37 0252450A<br />
 *    Q_x = 0679 834CEFB7 215DC365<br />
 *    Q_y = 4084 BC50388C 4E6FDFAB<br />
 * <br />
 * <br />
 *    ======= ECCp-89 ======= <br />
 *    p = 0158685C 903F1643 908BA955<br /> 
 *    a = 0C8AE4F7 DE8918AA 9FAB2260 <br />
 *    b = 00647E7E A1062AE6 9A7D1037 <br />
 *    P_x = 00C031D8 75DBF8E6 0BE95B0A <br />
 *    P_y = 0006F82C 1F879745 BF676D0A <br />
 *    Q_x = 00DE1AA9 4FF94DB6 4E763E2D <br />
 *    Q_y = 002A44C4 C2D4EE27 FA0A4BA9 <br />
 * <br />
 * <br />
 *    ======= ECCp-97 ======= <br />
 *    p = 01 6EA1595E D21AE4D8 D8420E35 <br />
 *    a = 00 47370916 A603B076 57C305C4 <br />
 *    b = 01 124DF86D 04064F50 3D9925AF <br />
 *    P_x = 00 D5D9E9DF F58A9232 A2749EBC <br />
 *    P_y = 01 1B34AE5A AB7C7AE5 5D6ABDB5 <br />
 *    Q_x = 00 DF7E84C4 2FEF50C5 316C508A <br />
 *    Q_y = 00 F259BC58 3729DA0F E8B97336 <br />
 * <br />
 * <br />
 *    ======== ECCp-109 ======== <br />
 *    p = 1BD5 79792B38 0B5B521E 6D9FB599 <br />
 *    a = 0FD4 C926FD17 8E9805E6 63021744 <br />
 *    b = 153D 3CBB508F FE3A7F31 FF4FAFFD <br />
 *    P_x = 04CC 974EBBCB FDC3636F EB9F11C7 <br />
 *    P_y = 0761 1B0EB122 9C0BFC5F 35521692 <br />
 *    Q_x = 0233 857E4E8B 5F005512 6E7D7B7C <br />
 *    Q_y = 19C8 C91063EB 4276371D 68B6B4D9 <br />
 * <br />
 * <br />
 *    ======== ECCp-131 ======== <br />
 *    p = 04 8E1D43F2 93469E33 194C4318 6B3ABC0B <br />
 *    a = 04 1CB121CE 2B31F608 A76FC8F2 3D73CB66 <br />
 *    b = 02 F74F717E 8DEC9099 1E5EA9B2 FF03DA58 <br />
 *    P_x = 03 DF84A96B 5688EF57 4FA91A32 E197198A <br />
 *    P_y = 01 47211619 17A44FB7 B4626F36 F0942E71 <br />
 *    Q_x = 03 AA6F004F C62E2DA1 ED0BFB62 C3FFB568 <br />
 *    Q_y = 00 9C21C284 BA8A445B B2701BF5 5E3A67ED <br />
 * <br />
 * <br />
 *    ======== ECCp-163 ======== <br />
 *    p = 05 177B8A2A 0FD6A4FF 55CDA06B 0924E125 F86CAD9B <br />
 *    a = 04 3182D283 FCE38807 30C9A2FD D3F60165 29A166AF <br />
 *    b = 02 0C61E945 9E53D887 1BCAADC2 DFC8AD52 25228035 <br />
 *    P_x = 00 17E70122 77E1B4E4 3F7BF746 57E8BE08 BACA175B <br />
 *    P_y = 00 AA03A0A8 26907046 97E8C504 CB135B2B 6EEF3C83 <br />
 *    Q_x = 01 DC1E9A48 2085B3DF A722EB7A 541D5050 5ED31DCA <br />
 *    Q_y = 01 2D71ECC1 578BFBE2 03D0C2CE 238EB606 0ADCAA1E <br />
 * <br />
 * <br />
 *    ======== ECCp-191 ======== <br />
 *    p = 7DF5BB7B F830F63C 77667331 106F9001 B27D3994 1032F5E5 <br />
 *    a = 3BD4FDA0 0A3E52E1 AF5C9456 686AB1B9 6195810C 27C5B110 <br />
 *    b = 24D1D433 1F8651B0 52E8042F A4325588 6E09BEF9 D3174872 <br />
 *    P_x = 3B511BC3 229CB4AE 654DFBC6 3210E278 3E91F43A A68D0EF4 <br />
 *    P_y = 4619A505 395A031A 304C0B72 061099F3 D0840CA6 1DE2F4BC <br />
 *    Q_x = 1DA38EF4 CBA78B2C D1D31EB3 75BC9E19 34C62ACE D29C54EE <br />
 *    Q_y = 4F3CA5FF 71D32D54 72D7F9EC D39DEF45 517F3B87 6466C8F1 <br />
 * <br />
 * <br />
 *    ======== ECCp-239 ======== <br />
 *    p = 7CFB 4C973A86 CDAF8982 31E4960A CDBBF5B6 A9017DBE D75FFABD D892085D <br />
 *    a = 76D4 219CF749 8B5B471E 85BC4DAB A3CE47AD C806228F BB0BCE19 7C4F4556 <br />
 *    b = 4F09 11A649B9 8CD0D3F6 95695E44 743EA948 E70B78CA B2C24C4E 7D50E2B3 <br />
 *    P_x = 0D35 ED464403 B23CC681 F18534C1 4B6FA2AD E7720523 F5094AD9 BFBE4752 <br />
 *    P_y = 52F1 BC7C3C74 38A91099 FDD53666 A0185FB5 9688CA3E 38084090 3B589BEB <br />
 *    Q_x = 2193 DCEAE32B C6EF6165 3DE4F1A1 41C15A9A 6A1A7296 802A887E BC0C7667 <br />
 *    Q_y = 6429 7E89EE34 0CFF78A5 31998CC3 F3376AFD 3AE177DB E30B82C9 3045F79D <br />
 * <br />
 * <br />
 *    ======== ECCp-359 ========<br />
 *    p = 58 D8420DF5 5D2B2000 FE2A55A0 32AB225F 544F8CB6 9CDF219B 0E394237 21F32A19 9D58685C 903F1643 908BA969<br />
 *    a = 08 77AEBB17 71A6EEA1 A7681809 B6884681 8D6434ED F6B4EF23 81672DE2 CAE70CB1 BA3E6A5F BD6DE671 70E4FC62<br />
 *    b = 3A DE22E91F 88EC9316 5A5BA6F1 51AA1EF2 65FF5FD0 12F30B9A 2D12A0E2 C3F5D7E6 95DDB2FA 75DE2139 E61D8DC8<br />
 *    P_x = 2F 912B99AD 5D761593 C2CE9D24 54EE91EF D1C698A0 DA7C2EFE 0DB86964 06885E63 EDB5CD29 C2735EC1 2183312D<br />
 *    P_y = 33 5E0C161B AB13BC46 DE0CD4E0 BA17913B 9C1EE26A 3DCF9022 DE774318 96F329D8 283B3DC9 3C469564 F9043CAA<br />
 *    Q_x = 10 E3208F62 A90AE4AE F55EB0A7 1F733443 2AF091C5 E9D50461 70C9835E C1B92167 698DCD0B 8E9040BD C3AFA0B0<br />
 *    Q_y = 15 03887866 4A36573C 40D10B3F 5FCD999E E1B619BF A84614EF 172FEFD4 949F188E 39BB40E1 A767A6DF 7458A13D<br />
 * <br />
 * </code>
 */
public class ChallengeCurves {

    /**
     * You don't need to create an object of this class.
     */
    private ChallengeCurves( ) {

    }

    /**
     * The bit size of each of the curves underlying field.
     */
    public final static int[] bitSizes = {
        79, 89, 97, 109, 131, 163, 191, 239, 359
    };
    
    /**
     * The prime order of the field.
     */
    public final static String[] p = {
        "62CE5177412ACA899CF5",
        "0158685C903F1643908BA955",
        "016EA1595ED21AE4D8D8420E35",
        "1BD579792B380B5B521E6D9FB599",
        "048E1D43F293469E33194C43186B3ABC0B",
        "05177B8A2A0FD6A4FF55CDA06B0924E125F86CAD9B",
        "7DF5BB7BF830F63C77667331106F9001B27D39941032F5E5",
        "7CFB4C973A86CDAF898231E4960ACDBBF5B6A9017DBED75FFABDD892085D",
        "58D8420DF55D2B2000FE2A55A032AB225F544F8CB69CDF219B0E39423721F32A199D58685C903F1643908BA969"
    };
    
    /**
     * The parameter a describing the curve y^2 = x^3 + ax + b.
     */
    public final static String[] a = {
        "39C95E6DDDB1BC45733C",
        "006F39B6CC51504A8AC22E63",
        "0047370916A603B07657C305C4",
        "0FD4C926FD178E9805E663021744",
        "041CB121CE2B31F608A76FC8F23D73CB66",
        "043182D283FCE3880730C9A2FDD3F6016529A166AF",
        "3BD4FDA00A3E52E1AF5C9456686AB1B96195810C27C5B110",
        "76D4219CF7498B5B471E85BC4DABA3CE47ADC806228FBB0BCE197C4F4556",
        "0877AEBB1771A6EEA1A7681809B68846818D6434EDF6B4EF2381672DE2CAE70CB1BA3E6A5FBD6DE67170E4FC62"
    };    

    /**
     * The parameter b describing the curve y^2 = x^3 + ax + b.
     */    
    public final static String[] b = {
        "1F16D880E89D5A1C0ED1",
        "00647E7EA1062AE69A7D1037",
        "01124DF86D04064F503D9925AF",
        "153D3CBB508FFE3A7F31FF4FAFFD",
        "02F74F717E8DEC90991E5EA9B2FF03DA58",
        "020C61E9459E53D8871BCAADC2DFC8AD5225228035",
        "24D1D4331F8651B052E8042FA43255886E09BEF9D3174872",
        "4F0911A649B98CD0D3F695695E44743EA948E70B78CAB2C24C4E7D50E2B3",
        "3ADE22E91F88EC93165A5BA6F151AA1EF265FF5FD012F30B9A2D12A0E2C3F5D7E695DDB2FA75DE2139E61D8DC8"
    };
    
    /**
     * The domain parameter P.
     */
    public final static String[][] P = {
        {"315D4B201C208475057D", "035F3DF5AB370252450A"},
        {"00C031D875DBF8E60BE95B0A", "0006F82C1F879745BF676D0A"},
        {"00D5D9E9DFF58A9232A2749EBC", "011B34AE5AAB7C7AE55D6ABDB5"},
        {"04CC974EBBCBFDC3636FEB9F11C7", "07611B0EB1229C0BFC5F35521692"},
        {"03DF84A96B5688EF574FA91A32E197198A", "014721161917A44FB7B4626F36F0942E71"},
        {"0017E7012277E1B4E43F7BF74657E8BE08BACA175B", "00AA03A0A82690704697E8C504CB135B2B6EEF3C83"},
        {"3B511BC3229CB4AE654DFBC63210E2783E91F43AA68D0EF4", "4619A505395A031A304C0B72061099F3D0840CA61DE2F4BC"},
        {"0D35ED464403B23CC681F18534C14B6FA2ADE7720523F5094AD9BFBE4752", "52F1BC7C3C7438A91099FDD53666A0185FB59688CA3E380840903B589BEB"},
        {"2F912B99AD5D761593C2CE9D2454EE91EFD1C698A0DA7C2EFE0DB8696406885E63EDB5CD29C2735EC12183312D", "335E0C161BAB13BC46DE0CD4E0BA17913B9C1EE26A3DCF9022DE77431896F329D8283B3DC93C469564F9043CAA"}
    };
    
    /**
     * Some random private key's.
     */
    public final static String[] privateKey = {
        "02CE5177407B7258DC31",
        "0058685C903EF906D7F58D47",
        "006EA1595ED21AE98FB6CCA20D",
        "0BD579792B380B049C4D13A75AE5",
        "008E1D43F293469E317F7ED728F6B8E6F1",
        "00177B8A2A0FD6A4FF55CCA7B8A1E21C88BD53B2C1",
        "0DF5BB7BF830F63C776673315F1259168CF997380ACA72C3",
        "0CFB4C973A86CDAF898231E4960ACCB3E442837A1D551D28F3B495F5EC5F",
        "08D8420DF55D2B2000FE2A55A032AB225F544F8CB69CD0BE1504766B9DD626631A535BA1BA6CB8D062F94102ED"
    };

    /**
     * Public keys Q.
     */
    public final static String[][] Q = {
            {"315D4B201C208475057D", "035F3DF5AB370252450A"},
            {"00C031D875DBF8E60BE95B0A", "0006F82C1F879745BF676D0A"},
            {"00D5D9E9DFF58A9232A2749EBC", "011B34AE5AAB7C7AE55D6ABDB5"},
            {"04CC974EBBCBFDC3636FEB9F11C7", "07611B0EB1229C0BFC5F35521692"},
            {"03DF84A96B5688EF574FA91A32E197198A", "014721161917A44FB7B4626F36F0942E71"},
            {"0017E7012277E1B4E43F7BF74657E8BE08BACA175B", "00AA03A0A82690704697E8C504CB135B2B6EEF3C83"},
            {"3B511BC3229CB4AE654DFBC63210E2783E91F43AA68D0EF4", "4619A505395A031A304C0B72061099F3D0840CA61DE2F4BC"},
            {"0D35ED464403B23CC681F18534C14B6FA2ADE7720523F5094AD9BFBE4752", "52F1BC7C3C7438A91099FDD53666A0185FB59688CA3E380840903B589BEB"},
            {"10E3208F62A90AE4AEF55EB0A71F7334432AF091C5E9D5046170C9835EC1B92167698DCD0B8E9040BDC3AFA0B0", "15038878664A36573C40D10B3F5FCD999EE1B619BFA84614EF172FEFD4949F188E39BB40E1A767A6DF7458A13D"}
    };
    
}
