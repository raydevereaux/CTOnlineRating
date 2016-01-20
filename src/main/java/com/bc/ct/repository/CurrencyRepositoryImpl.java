package com.bc.ct.repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.bc.ct.beans.CurrencyExchangeRate;
import com.google.common.collect.Iterables;

/**
 * Reads currency from MOM.  Note that MOM only stores CAD to USD so it is hard coded.  If you want USD
 * to CAD, use 1/rate.
 * @author dda07o
 */
@Repository
public class CurrencyRepositoryImpl implements CurrencyRepository {

	@Autowired
	private DataSource momDataSource;
	@Value("${momSchema}")
	private String momSchema;
	
	@Override
	public BigDecimal readCurrencyExchangeRate(int year, int month, String fromCurrency, String toCurrency) {
		ReadCurrencyExchangeRates proc = new ReadCurrencyExchangeRates(momDataSource);
		return proc.execute(year, month, fromCurrency, toCurrency);
	}
	
	private class ReadCurrencyExchangeRates extends StoredProcedure{
		// Also used in BPA. If you make changes here, need to update BPA as well!!
		public String SQL = momSchema + ".ReadCurExchRate";

		public ReadCurrencyExchangeRates(DataSource ds) {
			setDataSource(ds);
			setFunction(false);
			setSql(SQL);

			declareParameter(new SqlReturnResultSet("exchangeRates", new CurrencyRateMapper()));

			declareParameter(new SqlOutParameter("sqlstate", Types.CHAR));
			declareParameter(new SqlOutParameter("sqlcode", Types.INTEGER));
			declareParameter(new SqlOutParameter("return code", Types.INTEGER));
			declareParameter(new SqlOutParameter("return message", Types.VARCHAR));
			declareParameter(new SqlParameter("yearNbr", Types.DECIMAL));
			declareParameter(new SqlParameter("moNbr", Types.DECIMAL));
			declareParameter(new SqlParameter("toCurrencyCd", Types.VARCHAR));
			declareParameter(new SqlParameter("fromCurrencyCd", Types.VARCHAR));
			compile();
		}

		public BigDecimal execute(int year, int month, String fromCurrency, String toCurrency){
			Map <String, Object> inParams = new HashMap <String,Object>();

			inParams.put("yearNbr", year);
			inParams.put("moNbr", month);
			inParams.put("toCurrencyCd", toCurrency);
			inParams.put("fromCurrencyCd", fromCurrency);

			Map<String, Object> out = execute(inParams);

			@SuppressWarnings("unchecked")
			Collection<CurrencyExchangeRate> rates = (Collection<CurrencyExchangeRate>) out.get("exchangeRates");
			CurrencyExchangeRate rate = Iterables.getFirst(rates, new CurrencyExchangeRate());
			return rate.getCurrentMonthRate();
		}

		private class CurrencyRateMapper implements RowMapper<CurrencyExchangeRate> {
			public CurrencyExchangeRate mapRow(ResultSet rs, int rowNum) throws SQLException {
				CurrencyExchangeRate cer = new CurrencyExchangeRate();
				cer.setYear(rs.getInt(1));
				cer.setMonth(rs.getInt(2));
				cer.setCurrencyCd(StringUtils.trimWhitespace(rs.getString(3)));
				cer.setCurrentMonthRate(rs.getBigDecimal(4));
				cer.setPrevMonthRate(rs.getBigDecimal(5));
				cer.setLastUpdtUid(StringUtils.trimWhitespace(rs.getString(6)));
				cer.setLastUpdtTs(new DateTime(rs.getTimestamp(7)));
				cer.setFromCurrencyCd(StringUtils.trimWhitespace(rs.getString(8)));
				return cer;
			}
		}    
	}
}
