package com.app.tributum.activity.payments.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tributum.R;
import com.app.tributum.activity.payments.model.PaymentModel;
import com.app.tributum.listener.PaymentsItemClickListener;
import com.app.tributum.listener.RecyclerViewInputListener;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.ValidationUtils;
import com.app.tributum.utils.ui.CustomButton;

import java.util.List;

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.ItemViewHolder> {

    /**
     * List containing the payments displayed on the screen
     */
    private List<PaymentModel> paymentList;

    /**
     * Listener used when user clicks on the item image at the right side of the screen
     */
    private PaymentsItemClickListener paymentsItemClickListener;

    private RecyclerViewInputListener recyclerViewInputListener;

    /**
     * class constructor
     *
     * @param paymentList               the list containing payments
     * @param paymentsItemClickListener nameEditTextListener used when user presses the image on the right side of the row
     */
    public PaymentsAdapter(List<PaymentModel> paymentList, PaymentsItemClickListener paymentsItemClickListener, RecyclerViewInputListener recyclerViewInputListener) {
        this.paymentList = paymentList;
        this.paymentsItemClickListener = paymentsItemClickListener;
        this.recyclerViewInputListener = recyclerViewInputListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_list_item, parent, false);
        return new ItemViewHolder(view, new NameEditTextListener(), new PpsEditTextListener(), new AmountEditTextListener(), new SiteEditTextListener());
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        PaymentModel paymentModel = paymentList.get(holder.getAdapterPosition());

        holder.nameEditText.addTextChangedListener(holder.nameEditTextListener);
        holder.ppsEditText.addTextChangedListener(holder.ppsEditTextListener);
        holder.amountEditText.addTextChangedListener(holder.amountEditTextListener);
        holder.siteEditText.addTextChangedListener(holder.siteEditTextListener);

        holder.nameEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.ppsEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.amountEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.siteEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.nameEditText.setText(paymentModel.getName());
        holder.amountEditText.setText(paymentModel.getAmount());

        UtilsGeneral.setMaxLengthAndAllCapsToEditText(holder.ppsEditText, 9, true);
        holder.nameEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        holder.ppsEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        holder.amountEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        holder.ppsEditText.setText(paymentModel.getPps());
        holder.siteEditText.setText(paymentModel.getSite());
        if (holder.getAdapterPosition() == getItemCount() - 1) {
            holder.siteEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        } else {
            holder.siteEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        }

        if (getItemCount() == 1) {
            holder.removeButton.setVisibility(View.GONE);
        } else {
            holder.removeButton.setVisibility(View.VISIBLE);
            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleItemImageClick(holder);
                }
            });
        }
        if (paymentModel.isNameFocus()) {
            paymentModel.setNameFocus(false);
            UtilsGeneral.setFocusOnInput(holder.nameEditText);
        } else if (paymentModel.isPpsFocus()) {
            paymentModel.setPpsFocus(false);
            UtilsGeneral.setFocusOnInput(holder.ppsEditText);
        } else if (paymentModel.isAmountFocus()) {
            paymentModel.setAmountFocus(false);
            UtilsGeneral.setFocusOnInput(holder.amountEditText);
        } else if (paymentModel.isSiteFocus()) {
            paymentModel.setSiteFocus(false);
            UtilsGeneral.setFocusOnInput(holder.siteEditText);
        }
    }

    /**
     * Handles the action that will take place when clicking on the image on the right side of the row
     *
     * @param holder the item holder
     */
    private void handleItemImageClick(ItemViewHolder holder) {
        holder.nameEditText.removeTextChangedListener(holder.nameEditTextListener);
        holder.ppsEditText.removeTextChangedListener(holder.ppsEditTextListener);
        holder.amountEditText.removeTextChangedListener(holder.amountEditTextListener);
        if (paymentsItemClickListener != null)
            paymentsItemClickListener.removeItem(holder.getAdapterPosition());
    }

    public boolean areThereEmptyInputs() {
        for (PaymentModel model : paymentList) {
            if (model.getName().trim().equals("") || model.getPps().trim().equals("")
                    || model.getAmount().trim().equals("")
                    || model.getSite().trim().equals("")
                    || !ValidationUtils.isPpsValid(model.getPps())) {
                return true;
            }
        }
        return false;
    }

    public void findEmptyInputs() {
        if (recyclerViewInputListener == null)
            return;

        for (int i = 0; i < getItemCount(); i++) {
            PaymentModel model = paymentList.get(i);
            if (model.getName().trim().equals("")) {
                notifyDataSetChanged();
                recyclerViewInputListener.scrollToNameItem(i);
                break;
            } else if (model.getName().trim().equals("") || !ValidationUtils.isPpsValid(model.getPps().trim())) {
                notifyDataSetChanged();
                recyclerViewInputListener.scrollToPpsItem(i);
                break;
            } else if (model.getAmount().trim().equals("")) {
                notifyDataSetChanged();
                recyclerViewInputListener.scrollToAmountItem(i);
                break;
            } else if (model.getSite().trim().equals("")) {
                notifyDataSetChanged();
                recyclerViewInputListener.scrollToSiteItem(i);
                break;
            }
        }
    }

    public List<PaymentModel> getPaymentList() {
        return paymentList;
    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    public void addModel() {
        paymentList.add(new PaymentModel("", "", "", ""));
        notifyDataSetChanged();
    }

    public void remove(int position) {
        paymentList.remove(position);
        notifyDataSetChanged();
    }

    /**
     * Item holder class for the row
     */
    class ItemViewHolder extends RecyclerView.ViewHolder {

        EditText nameEditText;

        EditText ppsEditText;

        EditText amountEditText;

        EditText siteEditText;

        CustomButton removeButton;

        NameEditTextListener nameEditTextListener;

        PpsEditTextListener ppsEditTextListener;

        AmountEditTextListener amountEditTextListener;

        SiteEditTextListener siteEditTextListener;

        ItemViewHolder(@NonNull View itemView,
                       NameEditTextListener nameEditTextListener,
                       PpsEditTextListener ppsEditTextListener,
                       AmountEditTextListener amountEditTextListener,
                       SiteEditTextListener siteEditTextListener) {
            super(itemView);
            nameEditText = itemView.findViewById(R.id.payment_beneficiary_edit_text);
            ppsEditText = itemView.findViewById(R.id.payment_pps_edit_text);
            amountEditText = itemView.findViewById(R.id.payment_amount_edit_text);
            siteEditText = itemView.findViewById(R.id.payment_site_edit_text);
            removeButton = itemView.findViewById(R.id.remove_payment_text);

            this.nameEditTextListener = nameEditTextListener;
            nameEditText.addTextChangedListener(nameEditTextListener);
            this.ppsEditTextListener = ppsEditTextListener;
            ppsEditText.addTextChangedListener(ppsEditTextListener);
            this.amountEditTextListener = amountEditTextListener;
            amountEditText.addTextChangedListener(amountEditTextListener);
            this.siteEditTextListener = siteEditTextListener;
            siteEditText.addTextChangedListener(siteEditTextListener);
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
            paymentList.get(position).setNameFocus(charSequence.toString().equals(""));
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
            paymentList.get(position).setPpsFocus(charSequence.toString().equals("") || !ValidationUtils.isPpsValid(charSequence.toString()));
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
            paymentList.get(position).setAmountFocus(charSequence.toString().equals(""));
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no implementation needed
        }
    }

    /**
     * Custom listener for amount edit text
     */
    public class SiteEditTextListener implements TextWatcher {
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
            paymentList.get(position).setSite(charSequence.toString());
            paymentList.get(position).setSiteFocus(charSequence.toString().equals(""));
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no implementation needed
        }
    }
}