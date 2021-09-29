package com.app.tributum.listener;

/**
 * Interface used to decide what to happen when the user clicks on the image placed on the right sade of the item
 */
public interface PaymentsItemClickListener {

    /**
     * will remove the item given at {@code position}
     * @param position the position of the item that needs to be deleted
     */
    void removeItem(int position);
}