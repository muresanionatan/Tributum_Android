package com.example.tributum.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tributum.R;
import com.example.tributum.listener.PaymentsItemClickListener;
import com.example.tributum.model.PaymentModel;
import com.example.tributum.utils.UtilsGeneral;

import java.util.List;

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.ItemViewHolder> {

    /**
     * List containing the payments displayed on the screen
     */
    private List<PaymentModel> paymentList;

    /**
     * Listener used when user clicks on the item image at the right side of the screen
     */
    private PaymentsItemClickListener listener;

    /**
     * class constructor
     *
     * @param paymentList the list containing payments
     * @param listener    nameEditTextListener used when user presses the image on the right side of the row
     */
    public PaymentsAdapter(List<PaymentModel> paymentList, PaymentsItemClickListener listener) {
        this.paymentList = paymentList;
        this.listener = listener;
    }

    public void setList(List<PaymentModel> paymentList) {
        this.paymentList = paymentList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_list_item, parent, false);
        return new ItemViewHolder(view, new NameEditTextListener(), new PpsEditTextListener(), new AmountEditTextListener());
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        PaymentModel paymentModel = paymentList.get(holder.getAdapterPosition());
        holder.nameEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.ppsEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.amountEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.nameEditText.setText(paymentModel.getName());
        holder.amountEditText.setText(paymentModel.getAmount());

        UtilsGeneral.setMaxLengthAndAllCapsToEditText(holder.ppsEditText, 9, true);
        holder.nameEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        holder.ppsEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        holder.ppsEditText.setText(paymentModel.getPps());
        if (holder.getAdapterPosition() == getItemCount() - 1) {
            holder.amountEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        } else {
            holder.amountEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        }

        if (holder.getAdapterPosition() < getItemCount() - 1) {
            holder.addRemoveImage.setImageResource(R.drawable.remove_svg);
        } else {
            holder.addRemoveImage.setImageResource(R.drawable.add_svg);
        }
        holder.addRemoveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleItemImageClick(holder);
            }
        });

        if (position == getItemCount() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Handles the action that will take place when clicking on the image on the right side of the row
     *
     * @param holder the item holder
     */
    private void handleItemImageClick(ItemViewHolder holder) {
        if (listener == null)
            return;

        if (holder.getAdapterPosition() == getItemCount() - 1) {
            listener.addItem();
        } else {
            holder.nameEditText.removeTextChangedListener(holder.nameEditTextListener);
            listener.removeItem(holder.getAdapterPosition());
        }
    }

    public boolean areThereEmptyInputs() {
        for (PaymentModel model : paymentList) {
            if (model.getName().trim().equals("") || model.getAmount().trim().equals("")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    /**
     * Item holder class for the row
     */
    class ItemViewHolder extends RecyclerView.ViewHolder {

        EditText nameEditText;

        EditText ppsEditText;

        EditText amountEditText;

        ImageView addRemoveImage;

        View divider;

        NameEditTextListener nameEditTextListener;

        PpsEditTextListener ppsEditTextListener;

        AmountEditTextListener amountEditTextListener;

        ItemViewHolder(@NonNull View itemView, NameEditTextListener nameEditTextListener,
                       PpsEditTextListener ppsEditTextListener,
                       AmountEditTextListener amountEditTextListener) {
            super(itemView);
            nameEditText = itemView.findViewById(R.id.payment_beneficiary_edit_text);
            ppsEditText = itemView.findViewById(R.id.payment_pps_edit_text);
            amountEditText = itemView.findViewById(R.id.payment_amount_edit_text);
            addRemoveImage = itemView.findViewById(R.id.add_new_payment_image);
            divider = itemView.findViewById(R.id.divider);

            this.nameEditTextListener = nameEditTextListener;
            nameEditText.addTextChangedListener(nameEditTextListener);
            this.ppsEditTextListener = ppsEditTextListener;
            ppsEditText.addTextChangedListener(ppsEditTextListener);
            this.amountEditTextListener = amountEditTextListener;
            amountEditText.addTextChangedListener(amountEditTextListener);
        }
    }

    /**
     * Custom listener for name edit text
     */
    public class NameEditTextListener implements TextWatcher {
        private int position;

        void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no implementation needed
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            paymentList.get(position).setName(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no implementation needed
        }
    }

    /**
     * Custom listener for pps edit text
     */
    public class PpsEditTextListener implements TextWatcher {
        private int position;

        void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no implementation needed
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            paymentList.get(position).setPps(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no implementation needed
        }
    }

    /**
     * Custom listener for amount edit text
     */
    public class AmountEditTextListener implements TextWatcher {
        private int position;

        void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no implementation needed
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            paymentList.get(position).setAmount(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no implementation needed
        }
    }
}