package be.mowglilab.shopmylist.activities;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import be.mowglilab.shopmylist.R;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}

	@Override
	protected void onStart() {
		super.onStart();

		PackageInfo pInfo = null;
		try {
			pInfo = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Resources res = this.getResources();
		TextView versionTextView = (TextView) this
				.findViewById(R.id.VersionText);
		versionTextView.setText(String.format(
				res.getString(R.string.text_version_description),
				pInfo.versionName));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
