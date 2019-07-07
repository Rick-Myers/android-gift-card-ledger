package com.rickmyers.giftcardledger;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.UUID;

/**
 * A Fragment responsible for creating an Alert Dialog and asking the user if it's ok to delete a {@link GiftCard}.
 *
 * @author Rick Myers
 */
public class DeleteCardFragment extends AppCompatDialogFragment {

    public static final String EXTRA_DELETE = "com.rickmyers.giftcardledger.delete";
    private static final String ARG_DELETE = "delete";
    private UUID mId;

    /**
     * Creates an instance of a {@link DeleteCardFragment}. The {@link Bundle} will contain the UUID
     * of the {@link GiftCard}.
     *
     * @param id the gift card's {@link UUID}
     * @return the Delete Card dialog fragment.
     */
    public static DeleteCardFragment newInstance(UUID id) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DELETE, id);

        DeleteCardFragment fragment = new DeleteCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called by the Activity when the Fragment is created.
     *
     * @param savedInstanceState the Bundle used to host Fragment data.
     * @return the new {@link Dialog}
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mId = (UUID) getArguments().getSerializable(ARG_DELETE);
        GiftCardLedger mGiftCardLedger = GiftCardLedger.get(getActivity());
        GiftCard card = mGiftCardLedger.getGiftCard(mId);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.card_delete_question)
                .setMessage(card.getName())
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sendResult(Activity.RESULT_OK, mId);
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED, mId);
                    }
                })
                .create();
    }

    /**
     * Send the results of the dialog activity back to the target fragment.
     *
     * @param resultCode the result code of the dialog's activity.
     * @param id         the gift card's {@link UUID}
     */
    private void sendResult(int resultCode, UUID id) {
        // check if calling Fragment exists, return null if it does not.
        if (getTargetFragment() == null) {
            return;
        }
        // create an Intent with the card that was deleted
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DELETE, id);

        // call back to Fragment with results
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
