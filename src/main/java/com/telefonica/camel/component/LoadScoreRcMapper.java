package com.telefonica.camel.component;

import com.telefonica.camel.common.DecimalFormat;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoadScoreRcMapper extends LoadMapper {

    private static final String	DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final String	DATE_FORMAT	 = "dd/MM/yyyy";

    protected Map<String, Object> mapper(String[] cols) {


	Map<String, Object> row = new HashMap<>();
	row.put("documentType", cols[0]);
	row.put("documentNumber", cols[1]);
	row.put("actionType", cols[2]);
	row.put("portability", cols[3]);
	row.put("contextServiceIdentifier", cols[4]);
	row.put("score", cols[5]);
	row.put("scoreMessage", cols[6]);
	row.put("queryIdScoring", cols[7]);
	row.put("creditLimit", cols[8]);
	row.put("financingCapacity", cols[9]);
	row.put("numberOfLines", cols[10]);
	row.put("restrictions", cols[11]);
	row.put("action", cols[12]);
	return row;
    }

}