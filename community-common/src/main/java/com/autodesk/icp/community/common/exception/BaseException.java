//
// Copyright (C) 2015 by Autodesk, Inc. All Rights Reserved.
//
// The information contained herein is confidential, proprietary
// to Autodesk, Inc., and considered a trade secret as defined
// in section 499C of the penal code of the State of California.
// Use of this information by anyone other than authorized
// employees of Autodesk, Inc. is granted only under a written
// non-disclosure agreement, expressly prescribing the scope
// and manner of such use.
//
// AUTODESK MAKES NO WARRANTIES, EXPRESS OR IMPLIED, AS TO THE
// CORRECTNESS OF THIS CODE OR ANY DERIVATIVE WORKS WHICH INCORPORATE
// IT. AUTODESK PROVIDES THE CODE ON AN "AS-IS" BASIS AND EXPLICITLY
// DISCLAIMS ANY LIABILITY, INCLUDING CONSEQUENTIAL AND INCIDENTAL
// DAMAGES FOR ERRORS, OMISSIONS, AND OTHER PROBLEMS IN THE CODE.
//
// Use, duplication, or disclosure by the U.S. Government is subject
// to restrictions set forth in FAR 52.227-19 (Commercial Computer
// Software Restricted Rights) and DFAR 252.227-7013(c)(1)(ii)
// (Rights in Technical Data and Computer Software), as applicable.
//
package com.autodesk.icp.community.common.exception;

import com.autodesk.icp.community.common.util.JSONReadWriteHelper;

/**
 * <p>
 * This abstract class is the base class for an exception hierarchy within traffic modules. It has 2 subclasses:
 * {@link BusinessException} for user-related errors (ie errors triggered by invalid data submitted by the user and thus
 * correctable by the user) and {@link SystemException} for internal errors (ie system problems not the user's fault).
 * </p>
 * <p>
 * All Traffic exceptions contain an <i>error code</i>, <i>error message</i>, and optionally another
 * {@link java.lang.Throwable} object (representing a <i>root-cause error</i> or a <i>chained error</i>) and/or a
 * <i>localized message</i> (suitable for display to the user). You provide these when you construct the Traffic exception.
 * As follows:
 * </p>
 * <ul>
 * <li>
 * <p>
 * The <i>error code</i> you provide should be a short string codifying the particular error condition. 
 * </p>
 * </li>
 * <li>
 * <p>
 * The <i>error message</i> you provide should be an internal, not-meant-for-the-user-to-see string describing the error
 * condition. 
 * </p>
 * </li>
 * <li>
 * <p>
 * The {@link java.lang.Throwable} you provide represents the <i>next in chain</i>. It could represent the root-cause of
 * the particular error condition; or it could represent another exception in a series of related ones. It can itself be
 * another Traffic exception, or any other kind of Java exception or throwable.
 * </p>
 * </li>
 * <li>
 * <p>
 * Finally, the <i>localized message</i> is generated by looking up a localized message for your error code in your
 * message resource bundles. 
 * </p>
 * </li>
 * </ul>
 * 
 * @author Oliver
 */
public abstract class BaseException extends RuntimeException {
    private static final long serialVersionUID = -708067741229515162L;
    private String errorCode = null;
    private Throwable next = null;
    private String errorMessage = null;
    private String localizedMessage = null;

    /**
     * <p>
     * Construct an empty Traffic exception. All class attributes are null or empty.
     * </p>
     */
    public BaseException() {
        super();
        this.errorCode = null;
        this.errorMessage = null;
        this.localizedMessage = getMessage();
        this.next = null;
    }

    /**
     * <p>
     * Construct an Traffic exception containing just an error code. This constructor assumes the error message and
     * next-in-chain are null, and uses the error code in subsequent {@link #getMessage()} and
     * {@link #getLocalizedMessage()} calls. Generally these strings are not fully localized and suitable for display to
     * the user;
     * </p>
     * <p>
     * See the class documentation above, for more description of these attributes.
     * </p>
     * 
     * @param pErrorCode
     *            The error code (eg <code>forums.addNote.blank</code>)
     */
    public BaseException(String pErrorCode) {
        super(JSONReadWriteHelper.serializeToJSON(new error(pErrorCode, null)));
        this.errorCode = pErrorCode;
        this.errorMessage = null;
        this.localizedMessage = getMessage();
        this.next = null;
    }

    /**
     * <p>
     * Construct an Traffic exception containing an error code and an error message. This constructor assumes the
     * next-in-chain is null, and uses the error code and error message in subsequent {@link #getMessage()} and
     * {@link #getLocalizedMessage()} calls. Generally these strings are not fully localized and suitable for display to
     * the user;
     * </p>
     * <p>
     * See the class documentation above, for more description of these attributes.
     * </p>
     * 
     * @param pErrorCode
     *            The error code (eg <code>forums.addNote.blank</code>)
     * @param pErrorMessage
     *            The error message - eg: <code>New note text for forums thread cannot be blank.</code>
     */
    public BaseException(String pErrorCode, String pErrorMessage) {
        super(JSONReadWriteHelper.serializeToJSON(new error(pErrorCode, pErrorMessage)));
        this.errorCode = pErrorCode;
        this.errorMessage = pErrorMessage;
        this.localizedMessage = getMessage();
        this.next = null;
    }

    /**
     * <p>
     * Construct an Traffic exception containing an error code, error message, and some kind of next-in-chain
     * {@link java.lang.Throwable}. This constructor uses the error code, error message, and the next-in-chain message
     * (ie value of {@link java.lang.Throwable#getMessage()} in subsequent {@link #getMessage()} and
     * {@link #getLocalizedMessage()} calls. Generally these strings are not fully localized and suitable for display to
     * the user;
     * </p>
     * <p>
     * See the class documentation above, for more description of these attributes.
     * </p>
     * 
     * @param pErrorCode
     *            The error code (eg <code>forums.addNote.blank</code>)
     * @param pNext
     *            Some throwable (eg exception) which is the next in the chain (eg the root cause, or a related
     *            exception)
     * @param pErrorMessage
     *            The error message - eg: <code>New note text for forums thread cannot be blank.</code>
     */
    public BaseException(String pErrorCode, Throwable pNext, String pErrorMessage) {
        super(JSONReadWriteHelper.serializeToJSON(new error(pErrorCode, pErrorMessage)), pNext);
        this.errorCode = pErrorCode;
        this.errorMessage = pErrorMessage;
        this.localizedMessage = getMessage();
        this.next = pNext;
    }

    /**
     * Returns the error code provided to the constructor.
     * 
     * @return The error code.
     */
    public String getErrorCode() {
        return this.errorCode;
    }

    /**
     * Returns the root-cause throwable (eg, exception) provided to the constructor (null if none). This is the same
     * action as the {@link #getNext()} method since there is no distinction between a throwable which was given to the
     * constructor because it was a root-cause, versus one which was given to the constructor because it is a related
     * exception. This method is provided since it is part of the Java standard API for an exception.
     * 
     * @return The root-cause for this Traffic exception.
     */
    public Throwable getCause() {
        return getNext();
    }

    /**
     * Returns the next throwable (eg exception) in the chain of exceptions - this is the same throwable that was
     * provided to the constructor (null if none). This is the same action as the {@link #getCause()} method since there
     * is no distinction between a throwable which was given to the constructor because it was a root-cause, versus one
     * which was given to the constructor because it is a related exception - in both cases, the throwable is simply the
     * next one in the chain.
     * 
     * @return The root-cause for this Traffic exception.
     */
    public Throwable getNext() {
        return this.next;
    }

    /**
     * Returns the error message provided to the constructor.
     * 
     * @return
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Returns the localized error message determined by the constructor: either the one from your message resources
     * corresponding to the error code, or a default comprised of the error code, error message, and/or throwable
     * message (this default is also the same as the return value of {@link #getMessage()}). This method overrides the
     * default method in {@link java.lang.Throwable}. Please see the constructor and class documentation for more
     * information.
     * 
     * @return
     */
    public String getLocalizedMessage() {
        return this.localizedMessage;
    }

    /**
     * Returns a default message combining the error code, error message, and/or root-cause throwable message provided
     * to the constructor. Generally speaking, this message is not localized and not suitable for display to the user.
     */
    public String getMessage() {
        return super.getMessage();
    }
}

class error {
    private String errorcode;
    private String errormsg;
    
    public error(String errorcode, String errormsg) {
        this.errorcode = errorcode;
        this.errormsg = errormsg;
    }

    /**
     * @return the errorcode
     */
    public String getErrorcode() {
        return errorcode;
    }

    /**
     * @param errorcode the errorcode to set
     */
    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    /**
     * @return the errormsg
     */
    public String getErrormsg() {
        return errormsg;
    }

    /**
     * @param errormsg the errormsg to set
     */
    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }
}