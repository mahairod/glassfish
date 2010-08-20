/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * Copyright 2004-2005 Sun Microsystems, Inc.  All rights reserved.
 * Use is subject to license terms.
 */
package com.sun.jts.codegen.otsidl;


/**
* com/sun/jts/codegen/otsidl/_JControlStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from com/sun/jts/ots.idl
* Tuesday, February 5, 2002 12:57:23 PM PST
*/


//#-----------------------------------------------------------------------------
public class _JControlStub extends org.omg.CORBA.portable.ObjectImpl implements com.sun.jts.codegen.otsidl.JControl
{

  public org.omg.CosTransactions.otid_t getGlobalTID (org.omg.CosTransactions.StatusHolder status)
  {
    org.omg.CORBA.portable.InputStream $in = null;
    try {
       org.omg.CORBA.portable.OutputStream $out = _request ("getGlobalTID", true);
       $in = _invoke ($out);
       org.omg.CosTransactions.otid_t $result = org.omg.CosTransactions.otid_tHelper.read ($in);
       status.value = org.omg.CosTransactions.StatusHelper.read ($in);
       return $result;
    } catch (org.omg.CORBA.portable.ApplicationException $ex) {
       $in = $ex.getInputStream ();
       String _id = $ex.getId ();
       throw new org.omg.CORBA.MARSHAL (_id);
    } catch (org.omg.CORBA.portable.RemarshalException $rm) {
       return getGlobalTID (status);
    } finally {
        _releaseReply ($in);
    }
  } // getGlobalTID


  // transaction, and a value that indicates the state of the transaction.
  public long getLocalTID (org.omg.CosTransactions.StatusHolder status)
  {
    org.omg.CORBA.portable.InputStream $in = null;
    try {
       org.omg.CORBA.portable.OutputStream $out = _request ("getLocalTID", true);
       $in = _invoke ($out);
       long $result = $in.read_longlong ();
       status.value = org.omg.CosTransactions.StatusHelper.read ($in);
       return $result;
    } catch (org.omg.CORBA.portable.ApplicationException $ex) {
       $in = $ex.getInputStream ();
       String _id = $ex.getId ();
       throw new org.omg.CORBA.MARSHAL (_id);
    } catch (org.omg.CORBA.portable.RemarshalException $rm) {
       return getLocalTID (status);
    } finally {
        _releaseReply ($in);
    }
  } // getLocalTID


  // value that indicates the state of the transaction.
  public org.omg.CosTransactions.Status getTranState ()
  {
    org.omg.CORBA.portable.InputStream $in = null;
    try {
       org.omg.CORBA.portable.OutputStream $out = _request ("getTranState", true);
       $in = _invoke ($out);
       org.omg.CosTransactions.Status $result = org.omg.CosTransactions.StatusHelper.read ($in);
       return $result;
    } catch (org.omg.CORBA.portable.ApplicationException $ex) {
       $in = $ex.getInputStream ();
       String _id = $ex.getId ();
       throw new org.omg.CORBA.MARSHAL (_id);
    } catch (org.omg.CORBA.portable.RemarshalException $rm) {
       return getTranState ();
    } finally {
        _releaseReply ($in);
    }
  } // getTranState


  // Returns the state of the transaction as the Control object knows it.
  public void setTranState (org.omg.CosTransactions.Status state)
  {
    org.omg.CORBA.portable.InputStream $in = null;
    try {
       org.omg.CORBA.portable.OutputStream $out = _request ("setTranState", true);
       org.omg.CosTransactions.StatusHelper.write ($out, state);
       $in = _invoke ($out);
    } catch (org.omg.CORBA.portable.ApplicationException $ex) {
       $in = $ex.getInputStream ();
       String _id = $ex.getId ();
       throw new org.omg.CORBA.MARSHAL (_id);
    } catch (org.omg.CORBA.portable.RemarshalException $rm) {
       setTranState (state);
    } finally {
        _releaseReply ($in);
    }
  } // setTranState

  public org.omg.CosTransactions.Terminator get_terminator () throws org.omg.CosTransactions.Unavailable
  {
    org.omg.CORBA.portable.InputStream $in = null;
    try {
       org.omg.CORBA.portable.OutputStream $out = _request ("get_terminator", true);
       $in = _invoke ($out);
       org.omg.CosTransactions.Terminator $result = org.omg.CosTransactions.TerminatorHelper.read ($in);
       return $result;
    } catch (org.omg.CORBA.portable.ApplicationException $ex) {
       $in = $ex.getInputStream ();
       String _id = $ex.getId ();
       if (_id.equals ("IDL:omg.org/CosTransactions/Unavailable:1.0"))
          throw org.omg.CosTransactions.UnavailableHelper.read ($in);
       else
            throw new org.omg.CORBA.MARSHAL (_id);
    } catch (org.omg.CORBA.portable.RemarshalException $rm) {
       return get_terminator ();
    } finally {
        _releaseReply ($in);
    }
  } // get_terminator

  public org.omg.CosTransactions.Coordinator get_coordinator () throws org.omg.CosTransactions.Unavailable
  {
    org.omg.CORBA.portable.InputStream $in = null;
    try {
       org.omg.CORBA.portable.OutputStream $out = _request ("get_coordinator", true);
       $in = _invoke ($out);
       org.omg.CosTransactions.Coordinator $result = org.omg.CosTransactions.CoordinatorHelper.read ($in);
       return $result;
    } catch (org.omg.CORBA.portable.ApplicationException $ex) {
       $in = $ex.getInputStream ();
       String _id = $ex.getId ();
       if (_id.equals ("IDL:omg.org/CosTransactions/Unavailable:1.0"))
          throw org.omg.CosTransactions.UnavailableHelper.read ($in);
       else
            throw new org.omg.CORBA.MARSHAL (_id);
    } catch (org.omg.CORBA.portable.RemarshalException $rm) {
       return get_coordinator ();
    } finally {
        _releaseReply ($in);
    }
  } // get_coordinator

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:otsidl/JControl:1.0", 
    "IDL:omg.org/CosTransactions/Control:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.Object obj = org.omg.CORBA.ORB.init (args, props).string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     String str = org.omg.CORBA.ORB.init (args, props).object_to_string (this);
     s.writeUTF (str);
  }
} // class _JControlStub
