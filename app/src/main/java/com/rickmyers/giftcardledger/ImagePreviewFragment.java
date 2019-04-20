package com.rickmyers.giftcardledger;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.rickmyers.giftcardledger.utilities.PictureUtils;

import java.io.File;

public class ImagePreviewFragment extends DialogFragment {

    private static final String ARG_GIFTCARD_IMAGE = "giftcard_image";

    public static ImagePreviewFragment newInstance(File image){
        Bundle args = new Bundle();
        args.putSerializable(ARG_GIFTCARD_IMAGE, image);

        ImagePreviewFragment fragment = new ImagePreviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        File imageFile = (File) getArguments().getSerializable(ARG_GIFTCARD_IMAGE);

        Bitmap image = PictureUtils.getScaledBitmap(imageFile.getPath(), getActivity());

        View v =  LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fullsize_image, null);

        ImageView imageView = v.findViewById(R.id.card_picture);
        imageView.setImageBitmap(image);


        return new AlertDialog.Builder(getActivity()).setView(imageView).create();

     }

}
