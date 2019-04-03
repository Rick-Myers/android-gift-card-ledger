package com.rickmyers.giftcardledger;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import java.util.UUID;

public class DeleteCardFragment  extends AppCompatDialogFragment {

    public static final String EXTRA_DELETE = "com.rickmyers.giftcardledger.delete";
    private static final String ARG_DELETE = "delete";
    private UUID mId;

    public static DeleteCardFragment newInstance(UUID id){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DELETE, id);

        DeleteCardFragment fragment = new DeleteCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mId = (UUID) getArguments().getSerializable(ARG_DELETE);
        GiftCardLedger mGiftCardLedger = GiftCardLedger.get(getActivity());
        GiftCard card = mGiftCardLedger.getGiftCard(mId);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.card_balance_label)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sendResult(Activity.RESULT_OK, mId);
                    }
                })
                .create();//super.onCreateDialog(savedInstanceState);
    }

    private void sendResult(int resultCode, UUID id){
        if (getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DELETE, id);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
