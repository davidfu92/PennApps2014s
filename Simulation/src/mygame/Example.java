import java.util.Map;
import java.util.HashMap;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
public class SmsSender {
	public static final String ACCOUNT_SID = "AC123";
	public static final String AUTH_TOKEN = "456bef";
	public static void main(String[] args) throws TwilioRestException {
		TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
		Account account = client.getAccount();
		MessageFactory messageFactory = account.getMessageFactory();
		List<NameValuePair> params = new ArrayList<namevaluepair>();
		params.add(new BasicNameValuePair("To", "+17324704891")); // Replace with a valid phone number for your account.
		params.add(new BasicNameValuePair("From", "+17323877526")); // Replace with a valid phone number for your account.
		params.add(new BasicNameValuePair("Body", "Where's Wallace?"));
		Message sms = messageFactory.create(params);
	}
}
