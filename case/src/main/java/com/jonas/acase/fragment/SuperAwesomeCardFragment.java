/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jonas.acase.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jonas.acase.R;
import com.jonas.acase.eventbusmsg.PromptMsg;
import org.greenrobot.eventbus.EventBus;

public class SuperAwesomeCardFragment extends Fragment {

	private static final String ARG_POSITION = "position";

	private int position;

	public static SuperAwesomeCardFragment newInstance(int position) {
		SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View inflate = inflater.inflate(R.layout.fm_content, null);
		inflate.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				//隨機
				EventBus.getDefault().post(new PromptMsg(position,PromptMsg.RANDOM));
			}
		});
		inflate.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				//clear
				EventBus.getDefault().post(new PromptMsg(position,PromptMsg.CLEAR));
			}
		});
		inflate.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				//show
				EventBus.getDefault().post(new PromptMsg(position,PromptMsg.SHOW));
			}
		});
		return inflate;
	}

}