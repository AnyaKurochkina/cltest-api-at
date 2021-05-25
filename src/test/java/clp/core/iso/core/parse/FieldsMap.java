package clp.core.iso.core.parse;

import java.util.HashMap;

public class FieldsMap {
    public static final String MTI_FIELD = "message_type";
    public static final String TRAN_CODE_FIELD = "transaction_code";
    public static final String TRAN_FROM_FIELD = "from_account_type";
    public static final String TRAN_TO_FIELD = "to_account_type";
    static HashMap<String, String> map = new HashMap<>();

    static {
        FieldsMap.map.put("mti", "0");
        FieldsMap.map.put("card_number", "2");
        FieldsMap.map.put("message_type", "3");
        FieldsMap.map.put("transaction_amount", "4");
        FieldsMap.map.put("cardholder_billing_amount", "6");
        FieldsMap.map.put("transmission_datetime", "7");
        FieldsMap.map.put("cardholder_conversion_rate", "10");
        FieldsMap.map.put("system_trace_audit_number", "11");
        FieldsMap.map.put("local_transaction_time", "12");
        FieldsMap.map.put("local_transaction_date", "13");
        FieldsMap.map.put("merchant_category_code", "18");
        FieldsMap.map.put("acquiring_institution_country", "19");
        FieldsMap.map.put("pos_entry_mode", "22");
        FieldsMap.map.put("card_member", "23");
        FieldsMap.map.put("pos_condition_code", "25");
        FieldsMap.map.put("message_reason_code", "26");
        FieldsMap.map.put("acquirer_fee_amount", "28");
        FieldsMap.map.put("acquiring_institution_id", "32");
        FieldsMap.map.put("forwarding_institution_id", "33");
        FieldsMap.map.put("track2_data", "35");
        FieldsMap.map.put("rrn", "37");
        FieldsMap.map.put("authorization_code", "38");
        FieldsMap.map.put("response_code", "39");
        FieldsMap.map.put("card_acceptor_terminal_id", "41");
        FieldsMap.map.put("terminal_owner", "43.1");
        FieldsMap.map.put("terminal_city", "43.2");
        FieldsMap.map.put("terminal_state", "43.3");
        FieldsMap.map.put("terminal_country", "43.4");
        FieldsMap.map.put("terminal_address", "43.5");
        FieldsMap.map.put("terminal_branch", "43.6");
        FieldsMap.map.put("terminal_region", "43.7");
        FieldsMap.map.put("terminal_class", "43.8");
        FieldsMap.map.put("terminal_date", "43.9");
        FieldsMap.map.put("terminal_payment_system_name", "43.10");
        FieldsMap.map.put("terminal_financial_institution_name", "43.11");
        FieldsMap.map.put("terminal_retailer_name", "43.12");
        FieldsMap.map.put("terminal_county", "43.13");
        FieldsMap.map.put("terminal_zip_code", "43.14");
        FieldsMap.map.put("terminal_time_offset", "43.15");
        FieldsMap.map.put("pin_verification_result", "44.1");
        FieldsMap.map.put("cvv_verification_result", "44.2");
        FieldsMap.map.put("track1_data", "45");
        FieldsMap.map.put("other_transaction_rrn", "48.1");
        FieldsMap.map.put("other_transaction_pan", "48.2");
        FieldsMap.map.put("transaction_currency_code", "49");
        FieldsMap.map.put("billing_currency_code", "51");
        FieldsMap.map.put("pin", "52");
        FieldsMap.map.put("security_control_info", "53");
        FieldsMap.map.put("adjustment_amount", "54");
        FieldsMap.map.put("icc_system_data", "55");
        FieldsMap.map.put("issuing_institution_name", "61.1");
        FieldsMap.map.put("issuing_payment_system_name", "61.2");
        FieldsMap.map.put("external_transaction_attributes", "62");
        FieldsMap.map.put("new_pin", "63");
        FieldsMap.map.put("mac", "64");
        FieldsMap.map.put("network_management_information_code", "70");
        FieldsMap.map.put("replacement_amount", "95.1");
        FieldsMap.map.put("replacement_amount_orig", "95.2");
        FieldsMap.map.put("receiving_institution_id", "100");
        FieldsMap.map.put("from_account", "102");
        FieldsMap.map.put("to_account", "103");
        FieldsMap.map.put("host_net_id", "104");
        FieldsMap.map.put("ledger_balance", "105.1");
        FieldsMap.map.put("available_balance", "105.2");
        FieldsMap.map.put("balance_option", "105.3");
        FieldsMap.map.put("multi_currency_data", "106");
        FieldsMap.map.put("final_rrn", "107");
        FieldsMap.map.put("regional_listing_data", "108");
        FieldsMap.map.put("multi_account_data", "109");
        FieldsMap.map.put("numeric_message", "110");
        FieldsMap.map.put("payment_personal_attributes", "111");
        FieldsMap.map.put("mini_statement_data", "114");
        FieldsMap.map.put("statement_data", "115");
        FieldsMap.map.put("billing_data", "116");
        FieldsMap.map.put("additional_pos_data", "121");
        FieldsMap.map.put("card_3d_secure_data", "122");
        FieldsMap.map.put("misc_transaction_attributes_1", "123");
        FieldsMap.map.put("misc_transaction_attributes_2", "124");
        FieldsMap.map.put("admin_transaction_data", "125");
        FieldsMap.map.put("preauth_parameters", "126");
        FieldsMap.map.put("additional_info", "127");
        FieldsMap.map.put("msg_auth_code", "128");
    }

    public static String getISOField(final String profileFieldName) {
        return FieldsMap.map.get(profileFieldName);
    }

    public static boolean hasField(final String fieldName) {
        return FieldsMap.map.containsKey(fieldName);
    }
}
