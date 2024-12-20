package com.pucosa.pucopointManager.ui.agreement

import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import android.os.AsyncTask
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.pucosa.pucopointManager.databinding.FragmentWrittenAgreementBinding
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class WrittenAgreement : Fragment() {

    private lateinit var binding: FragmentWrittenAgreementBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        binding =  FragmentWrittenAgreementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val viewer: TextView = binding.writtenAgreement
//        val formattedText = ""
//        viewer.text = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)

        val viewer: TextView = binding.writtenAgreement

        val formattedText = "<html>\n" +
                "\n" +
                "\n" +
                "<body>\n" +
                "    <h1 style=\"padding-left: 111pt;text-indent: 0pt;text-align: center;\">PUCOPOINT TERMS AND CONDITIONS</h1>\n" +
                "    <p style=\"padding-top: 9pt;padding-left: 5pt;text-indent: 0pt;text-align: left;\">EFFECTIVE FROM 23 APRIL, 2022</p>\n" +
                "    <p style=\"text-indent: 0pt;text-align: left;\"><br /></p>\n" +
                "    <p style=\"padding-left: 5pt;text-indent: 0pt;line-height: 108%;text-align: left;\"><a\n" +
                "            href=\"mailto:PUCOREADS@PUCOSA.COM\" class=\"a\" target=\"_blank\"></a>IF THIS DOCUMENT IS NOT IN A LANGUAGE THAT YOU\n" +
                "            UNDERSTAND, YOU SHALL CONTACT PUCOREADS AT PUCOREADS@PUCOSA.COM. FAILURE TO DO SO WITHIN 12 (TWELVE)\n" +
                "        HOURS FROM THE TIME OF RECEIPT OF THIS DOCUMENT AND YOUR ACCEPTANCE OF THIS DOCUMENT BY CLICKING ON THE ‘I\n" +
                "        ACCEPT’ BUTTON SHALL BE CONSIDERED AS YOUR UNDERSTANDING OF THIS DOCUMENT.</p>\n" +
                "    <p style=\"text-indent: 0pt;text-align: left;\"><br /></p>\n" +
                "    <h2 style=\"padding-left: 5pt;text-indent: 0pt;text-align: left;\">SUBSCRIPTION AGREEMENT</h2>\n" +
                "    <p style=\"padding-top: 9pt;padding-left: 5pt;text-indent: 0pt;line-height: 108%;text-align: left;\">THIS DOCUMENT IS\n" +
                "        AN ELECTRONIC RECORD IN TERMS OF INFORMATION TECHNOLOGY ACT, 2000 AND RULES THERE UNDER AS APPLICABLE AND THE\n" +
                "        PROVISIONS PERTAINING TO ELECTRONIC RECORDS IN VARIOUS STATUTES AS AMENDED BY THE INFORMATION TECHNOLOGY ACT,\n" +
                "        2000. THIS ELECTRONIC RECORD IS GENERATED BY A COMPUTER SYSTEM AND DOES NOT REQUIRE ANY PHYSICAL OR DIGITAL\n" +
                "        SIGNATURES. BY CLICKING ON THE “I ACCEPT” BUTTON ON THIS ELECTRONIC CONTRACT, YOU ARE CONSENTING TO BE BOUND BY\n" +
                "        THIS SUBSCRIPTION AGREEMENT. PLEASE ENSURE THAT YOU READ AND UNDERSTAND ALL THE PROVISIONS OF THIS SUBSCRIPTION\n" +
                "        AGREEMENT BEFORE YOU START USING THE PORTAL, AS YOU SHALL BE BOUND BY ALL THE TERMS HEREIN UPON CLICKING ON THE\n" +
                "        “ACCEPT &amp; CONTINUE” BUTTON ON THIS ELECTRONIC CONTRACT. IF YOU DO NOT ACCEPT ANY OF THE TERMS CONTAINED\n" +
                "        HEREIN, THEN PLEASE DO NOT USE THE PORTAL OR AVAIL ANY OF THE SERVICES BEING PROVIDED THEREIN. YOUR AGREEMENT TO\n" +
                "        THE SUBSCRIPTION AGREEMENT SHALL OPERATE AS A BINDING AGREEMENT BETWEEN YOU AND PUCOREADS IN RESPECT OF THE\n" +
                "        SERVICES OF THE PORTAL.</p>\n" +
                "    <p style=\"padding-top: 7pt;padding-left: 5pt;text-indent: 0pt;line-height: 109%;text-align: left;\">THIS AGREEMENT\n" +
                "        AND OTHER TERMS AND POLICIES MAY CHANGE ANYTIME WITHOUT ANY PRIOR NOTICE, SO KINDLY STAY UPDATED ABOUT OUR\n" +
                "        POLICIES.</p>\n" +
                "    <h2 style=\"padding-top: 7pt;padding-left: 5pt;text-indent: 0pt;text-align: left;\">DEFINITIONS</h2>\n" +
                "    <p style=\"padding-top: 9pt;padding-left: 5pt;text-indent: 0pt;text-align: left;\">“You” refers to the pucopoint\n" +
                "        owner.</p>\n" +
                "    <p style=\"padding-top: 9pt;padding-left: 5pt;text-indent: 0pt;line-height: 109%;text-align: left;\">“Potential\n" +
                "        pucopoint owner” refers to the person who has shown interest in becoming a pucopoint owner and can be the owner\n" +
                "        of pucopoint in near future.</p>\n" +
                "    <p style=\"padding-top: 7pt;padding-left: 5pt;text-indent: 0pt;line-height: 168%;text-align: left;\">“We, our, us,\n" +
                "        company, Pucoreads” refers to the Pucoreads as a company. “Pucopoint” means the shop or place that is assigned\n" +
                "        to facilitate the exchange.</p>\n" +
                "    <p style=\"padding-left: 5pt;text-indent: 0pt;line-height: 107%;text-align: left;\">“Pucopoint owner” refers to the\n" +
                "        owner of the pucopoint who is registered along with that pucopoint at the time of registration.</p>\n" +
                "    <p style=\"padding-left: 5pt;text-indent: 0pt;line-height: 22pt;text-align: left;\">“Seller” refers to the person who\n" +
                "        is selling his book and comes to pucopoint to submit the book in order. “Buyer” refers to the person who has\n" +
                "        ordered the book and will be paying and collecting the book from</p>\n" +
                "    <p style=\"padding-left: 5pt;text-indent: 0pt;line-height: 13pt;text-align: left;\">pucopoint.</p>\n" +
                "    <h2 style=\"padding-top: 3pt;padding-left: 5pt;text-indent: 0pt;text-align: left;\">NOW THEREFORE THE PARTIES HERETO\n" +
                "        AGREE AS FOLLOWS:</h2>\n" +
                "    <ol id=\"l1\">\n" +
                "        <li >\n" +
                "            <h3 style=\"padding-top: 9pt;padding-left: 41pt;text-indent: -18pt;text-align: left;\">Scope and obligations\n" +
                "            </h3>\n" +
                "            <ol id=\"l2\">\n" +
                "                <li >\n" +
                "                    <p\n" +
                "                        style=\"padding-top: 1pt;padding-left: 77pt;text-indent: -18pt;line-height: 108%;text-align: left;\">\n" +
                "                        The execution of this agreement shall take effect just after the registration of Pucopoint owner\n" +
                "                        on the platform. Successful registration is completed only when a potential Pucopoint owner\n" +
                "                        accepts the terms on Pucopoint app.</p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 108%;text-align: left;\">We are\n" +
                "                        committed to ensuring the best possible circumstances for the Pucopoints to work. Pucopoint\n" +
                "                        owner needs to follow these terms to work with us. This will ensure the smooth working of the\n" +
                "                        system.</p>\n" +
                "                </li>\n" +
                "            </ol>\n" +
                "        </li>\n" +
                "        <li >\n" +
                "            <h3 style=\"padding-left: 41pt;text-indent: -18pt;line-height: 15pt;text-align: left;\">Behavior</h3>\n" +
                "            <ol id=\"l3\">\n" +
                "                <li >\n" +
                "                    <p\n" +
                "                        style=\"padding-top: 1pt;padding-left: 77pt;text-indent: -18pt;line-height: 107%;text-align: left;\">\n" +
                "                        Pucopoint owner must keep his cool in difficult situations with customer. Pucopoint owner must\n" +
                "                        not misbehave, abuse, and force any customer or our agents.</p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 108%;text-align: left;\">Pucopoint\n" +
                "                        owners must not misbehave or try to compete with other Pucopoint owners. We take the\n" +
                "                        responsibility to treat every Pucopoint equally and give equal opportunities to them. Any\n" +
                "                        Pucopoint who is not been given equal opportunities will be those who have shown any kind of\n" +
                "                        unacceptable behavior. We can take suitable actions against unacceptable behavior including\n" +
                "                        suspending the partnership with Pucopoint.</p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 107%;text-align: left;\">In case of any\n" +
                "                        problem, Pucopoint owner may contact us for help. We are not bound to solve every situation.\n" +
                "                        Actions will be taken accordingly in certain situations.</p>\n" +
                "                </li>\n" +
                "            </ol>\n" +
                "        </li>\n" +
                "        <li >\n" +
                "            <h3 style=\"padding-left: 41pt;text-indent: -18pt;text-align: left;\">Handling of Book</h3>\n" +
                "            <ol id=\"l4\">\n" +
                "                <li >\n" +
                "                    <p\n" +
                "                        style=\"padding-top: 1pt;padding-left: 77pt;text-indent: -18pt;line-height: 108%;text-align: left;\">\n" +
                "                        The pucopoint owner is responsible for keeping the book safe in his custody. Books need to be\n" +
                "                        delivered as it was submitted by the seller for an exchange. No of the pages should be the same\n" +
                "                        during delivery of the book to the buyer as it was when the seller submitted the book.</p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 107%;text-align: left;\">In case of a\n" +
                "                        return, the Pucopoint owner is responsible for delivering the unchanged book back to the seller.\n" +
                "                        No of the pages, conditions, or content of the book should be the same as when the seller\n" +
                "                        submitted the book before.</p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 107%;text-align: left;\">Pucopoint must\n" +
                "                        not try to photocopy or scan the pages or cover of the book. Violation of this term may put\n" +
                "                        Pucopoint owner in legal trouble.</p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 108%;text-align: justify;\">Pucopoint or\n" +
                "                        Pucopoint owner can claim no right to the ownership of the book at any time. In case of the\n" +
                "                        seller does not take the book back within 1 week from the return initiated, we will claim the\n" +
                "                        ownership and take the book from the Pucopoint into our custody.</p>\n" +
                "                </li>\n" +
                "            </ol>\n" +
                "        </li>\n" +
                "        <li >\n" +
                "            <h3 style=\"padding-left: 41pt;text-indent: -18pt;line-height: 15pt;text-align: justify;\">Payments</h3>\n" +
                "            <ol id=\"l5\">\n" +
                "                <li >\n" +
                "                    <p\n" +
                "                        style=\"padding-top: 1pt;padding-left: 77pt;text-indent: -18pt;line-height: 107%;text-align: left;\">\n" +
                "                        Pucopoint should also facilitate the exchange of money from the buyer to us. Buyers may make the\n" +
                "                        payment through online mode or cash mode.</p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 108%;text-align: left;\">Pucopoint can’t\n" +
                "                        claim that money, including its commission, until we have got our share in that. Commission on\n" +
                "                        every book for pucopoint will be decided by the company and will be communicated to you at the\n" +
                "                        time of registration. This commission is entitled to change in the future and the same will be\n" +
                "                        communicated to you when it happens.</p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 108%;text-align: left;\">Pucopoint owner\n" +
                "                        must transfer the money to us within 4 working days. Not being able to comply with this term may\n" +
                "                        force us to take necessary actions which include a fine and suspension. The suspension will only\n" +
                "                        be done after three warnings to the Pucopoint</p>\n" +
                "                    <p style=\"padding-top: 2pt;padding-left: 77pt;text-indent: 0pt;line-height: 107%;text-align: left;\">\n" +
                "                        owner. If the Pucopoint owner fails to comply with this term after that, he may get suspended.\n" +
                "                    </p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 107%;text-align: left;\">In case, the\n" +
                "                        Pucopoint owner refuses to pay back the remaining money, legal action will be taken immediately.\n" +
                "                    </p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 107%;text-align: left;\">Pucopoint must\n" +
                "                        not use those payments for illegal activities and activities beyond the working of the company\n" +
                "                        (Pucoreads).</p>\n" +
                "                </li>\n" +
                "            </ol>\n" +
                "        </li>\n" +
                "        <li >\n" +
                "            <h3 style=\"padding-left: 41pt;text-indent: -18pt;text-align: left;\">Privacy</h3>\n" +
                "            <ol id=\"l6\">\n" +
                "                <li >\n" +
                "                    <p style=\"padding-top: 1pt;padding-left: 77pt;text-indent: -18pt;text-align: left;\">Pucopoint must\n" +
                "                        not disclose the identity of the seller and buyer without their consent.</p>\n" +
                "                </li>\n" +
                "            </ol>\n" +
                "        </li>\n" +
                "        <li >\n" +
                "            <h3 style=\"padding-top: 1pt;padding-left: 41pt;text-indent: -18pt;text-align: left;\">Disclaimer</h3>\n" +
                "            <ol id=\"l7\">\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 108%;text-align: left;\">You agree that\n" +
                "                        Pucoreads manages the applications (Pucoreads and Pucopoint) and website, and is only\n" +
                "                        responsible for managing them. Bad behavior of customers is not the responsibility of the\n" +
                "                        company in any way.</p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 107%;text-align: left;\">You agree that\n" +
                "                        you are not involved in any illegal or non-ethical activities. If this is found to be true, you\n" +
                "                        agree that we can take any strict actions including suspension of the pucopoint and legal path.\n" +
                "                    </p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 107%;text-align: left;\">You agree that\n" +
                "                        you will comply with all the terms and conditions listed in this document. Also, you agree that\n" +
                "                        you will follow our privacy policy and return policy.</p>\n" +
                "                </li>\n" +
                "            </ol>\n" +
                "        </li>\n" +
                "        <li >\n" +
                "            <h3 style=\"padding-left: 41pt;text-indent: -18pt;text-align: left;\">Term and termination</h3>\n" +
                "            <ol id=\"l8\">\n" +
                "                <li >\n" +
                "                    <p\n" +
                "                        style=\"padding-top: 1pt;padding-left: 77pt;text-indent: -18pt;line-height: 109%;text-align: left;\">\n" +
                "                        This Agreement shall be valid for 5 (five) years and shall be renewed automatically unless\n" +
                "                        otherwise agreed between the Parties.</p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 108%;text-align: left;\">The Parties to\n" +
                "                        this Agreement shall be entitled to terminate this Agreement with prior written notice of 7\n" +
                "                        (seven) business days to the other Party without assigning any reason for the termination.</p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 109%;text-align: left;\">Pucoreads shall\n" +
                "                        be entitled to terminate this Agreement immediately for breach of any terms in this Agreement by\n" +
                "                        the Pucopoint or pucopoint owner.</p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 107%;text-align: left;\">Upon\n" +
                "                        termination of this Agreement, the registration of Pucopoint will be terminated immediately and\n" +
                "                        existing orders will be reassigned to other pucopoint.</p>\n" +
                "                </li>\n" +
                "            </ol>\n" +
                "        </li>\n" +
                "        <li >\n" +
                "            <h3 style=\"padding-left: 41pt;text-indent: -18pt;text-align: left;\">Governing Law and Dispute Resolution\n" +
                "            </h3>\n" +
                "            <ol id=\"l9\">\n" +
                "                <li >\n" +
                "                    <p\n" +
                "                        style=\"padding-top: 1pt;padding-left: 77pt;text-indent: -18pt;line-height: 108%;text-align: left;\">\n" +
                "                        If any dispute arises between the Pucopoint owner and Pucoreads, in connection with, or arising\n" +
                "                        out of, this Agreement, the dispute shall be referred to arbitration under the Arbitration and\n" +
                "                        Conciliation Act, 1996 (Indian) to be adjudicated by a sole arbitrator to be appointed by\n" +
                "                        Pucoreads. The proceedings of arbitration shall be in the English language. The arbitrator’s\n" +
                "                        award shall be final and binding on the Parties.</p>\n" +
                "                </li>\n" +
                "                <li >\n" +
                "                    <p style=\"padding-left: 77pt;text-indent: -18pt;line-height: 13pt;text-align: left;\">This Agreement\n" +
                "                        shall be governed by and construed under the laws of India.</p>\n" +
                "                </li>\n" +
                "            </ol>\n" +
                "        </li>\n" +
                "    </ol>\n" +
                "    <h2 style=\"padding-top: 9pt;padding-left: 5pt;text-indent: 0pt;text-align: left;\">Contact details:</h2>\n" +
                "    <p style=\"padding-top: 9pt;padding-left: 5pt;text-indent: 0pt;line-height: 168%;text-align: left;\"><a\n" +
                "            href=\"mailto:pucoreads@pucosa.com\" class=\"a\" target=\"_blank\"></a>Email: pucoreads@pucosa.com Mobile no:\n" +
                "        +918005189704</p>\n" +
                "    <p style=\"padding-left: 5pt;text-indent: 0pt;line-height: 166%;text-align: left;\">Whatsapp no: +918005189704\n" +
                "        Website: pucosa.com</p>\n" +
                "</body>\n" +
                "\n" +
                "</html>"
        viewer.text = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        navController = Navigation.findNavController(view)
        binding.back.setOnClickListener{
            navController.navigate(com.pucosa.pucopointManager.R.id.action_writtenAgreement_to_onboarding_agreement)
        }

    }

}