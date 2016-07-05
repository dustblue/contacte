package com.rakesh.contacte;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact>{

    Context context;
    ArrayList<Contact> data=new ArrayList<>();
    int layoutResId;

    public ContactAdapter (Context context, int layoutResId, ArrayList<Contact> data) {
        super(context, layoutResId, data);
        this.layoutResId = layoutResId;
        this.data = data;
        this.context = context;
    }

    public class ContactHolder
    {
        TextView nameText;
        TextView numberText;
        ImageView contactImage;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ContactHolder holder = new ContactHolder();
        LayoutInflater inflator = LayoutInflater.from(getContext());
        View contactRow = inflator.inflate(layoutResId, parent, false);

        holder.nameText = (TextView) contactRow.findViewById(R.id.nameText);
        holder.numberText = (TextView) contactRow.findViewById(R.id.numberText);
        holder.contactImage = (ImageView) contactRow.findViewById(R.id.contactImage);

        Contact contact = data.get(position);
        holder.nameText.setText(contact.contactName);
        holder.numberText.setText(contact.contactNumber);
        byte[] outImage=contact.contactImage;
        ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
        Bitmap theImage = BitmapFactory.decodeStream(imageStream);
        holder.contactImage.setImageBitmap(theImage);

        return contactRow;
    }
}
