/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.mihanjk.nopollenapp.presentation.settings.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.mihanjk.nopollenapp.R;


public class ChangePasswordDialogFragment extends DialogFragment {
    public static final String OLD_PASSWORD = "Old password";
    public static final String NEW_PASSWORD = "New password";
    public static final String CONFIRM_PASSWORD = "Confirm password";

    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmNewPassword;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.change_password_layout, null);
        oldPassword = (EditText) view.findViewById(R.id.old_password);
        newPassword = (EditText) view.findViewById(R.id.new_password);
        confirmNewPassword = (EditText) view.findViewById(R.id.confirm_new_password);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.change_password_title, (dialog, id) -> {
                    Intent intent = new Intent();
                    intent.putExtra(OLD_PASSWORD, oldPassword.getText().toString());
                    intent.putExtra(NEW_PASSWORD, newPassword.getText().toString());
                    intent.putExtra(CONFIRM_PASSWORD, confirmNewPassword.getText().toString());
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> ChangePasswordDialogFragment.this.getDialog().cancel());
        return builder.create();
    }
}
