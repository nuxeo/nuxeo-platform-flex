/*
 * (C) Copyright 2006-20011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */
package org.nuxeo.ecm.flex.login
{

  import mx.rpc.http.HTTPService;
  import mx.rpc.events.ResultEvent;
  import mx.rpc.events.FaultEvent;
  import mx.controls.Alert;

  public class NuxeoLogin
  {

    private var _service:HTTPService;
    private var _targetURL:String;
    private var _serverURL:String;
    private var _loginSuccessCB:Function;
    private var _loginFailedCB:Function;
    private var _logoutSuccessCB:Function;
    private var _user:Object;
    private var _cbLoginCheck:Function;
    private var _cbLogout:Function;

    public function NuxeoLogin()
    {
      _service = new HTTPService();
      _service.resultFormat= "e4x";
      //_service.concurrency="single";
      _targetURL="/nuxeo/flexlogin/";
      _serverURL="";
    }

    public function get targetUrl(): String
    {
      return _targetURL;
    }

    public function set targetURL(url:String): void
    {
      _targetURL=url;
    }

    public function get servertUrl(): String
    {
      return _serverURL;
    }

    public function set serverURL(url:String): void
    {
      _serverURL=url;
    }

    public function login(userName:String, password:String):void
    {
      _service.method="POST";
      _service.addEventListener(ResultEvent.RESULT, loginResultHandler);
      _service.url = _serverURL + _targetURL;
      var params:Object=new Object();
      params['user_name']=userName;
      params['user_password']=password;
      _service.send(params);
    }

    public function logout(cb:Function):void
    {
      _service.method="GET";
      if (cb==null)
         _cbLogout=cb;
      else
         _cbLogout=null;
      _service.addEventListener(ResultEvent.RESULT, logoutResultHandler);
      _service.url = _serverURL + "/nuxeo/logout";
      _service.send();
    }

    public function isLoggedIn(cb:Function):void
    {
      _service.method="GET";
      _cbLoginCheck=cb;
      _service.addEventListener(ResultEvent.RESULT, loginCheckResultHandler);
      _service.url = _targetURL;
      _service.send();
    }

    private function logoutResultHandler(event:ResultEvent):void
    {
      if (_cbLogout!=null)
         _cbLogout();
    }

    private function loginCheckResultHandler(event:ResultEvent):void
    {
      var loginResponse:String=_service.lastResult.status;
      if (loginResponse=="OK")
      {
        _user=new Object();
        _user=_service.lastResult.user;
        _cbLoginCheck(true);
      }
      else
        _cbLoginCheck(false);
    }

    private function loginResultHandler(event:ResultEvent):void
    {
      var loginResponse:String=_service.lastResult.status;

      if (loginResponse=="OK")
      {
        _user=new Object();
        _user=_service.lastResult.user;
         _loginSuccessCB(event);
      }
      else
        _loginFailedCB(new FaultEvent("Login Failed"));
    }

    public function setLoginSucessCallBack(cb:Function):void
    {
      _loginSuccessCB=cb;
    }

    public function setLoginFailedCallBack(cb:Function):void
    {
      _loginFailedCB=cb;
    }

    public function getConnectedUser():Object
    {
      return _user;
    }
  }
}