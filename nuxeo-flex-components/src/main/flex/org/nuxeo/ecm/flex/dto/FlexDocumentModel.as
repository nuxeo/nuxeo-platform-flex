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

package org.nuxeo.ecm.flex.dto
{
  import mx.collections.ArrayCollection;
  import flash.utils.IExternalizable;
  import flash.utils.IDataInput;
  import flash.utils.IDataOutput;
  import flash.events.EventDispatcher;
  import flash.events.Event;
  import mx.core.IUID;

  [RemoteClass(alias="org.nuxeo.ecm.flex.javadto.FlexDocumentModel")]
  public class FlexDocumentModel extends EventDispatcher implements IExternalizable, IUID

  {
    private var _docRef:String;
    private var _name:String;
    private var _path:String;
    private var _lifeCycleState:String;
    private var _data:Object;
    private var _dirty:Object;
    private var _type:String;
    private var _icon:String;
    private var _iconExpanded:String;
    private var _isFolder:Boolean;
    private var _isDirty:Boolean=false;

    public function FlexDocumentModel()
    {
      _name="init_from_flex";
    }

    public function setup(type:String,name:String,parentPath:String):void
    {
      _type=type;
      _name=name;
      _path=parentPath+_name;
      _dirty = new Object();
      _data = new Object();
    }

    public function get uid(): String
    {
      return _docRef;
    }

    public function set uid(uid:String): void
    {

    }
        
    public function readExternal(input:IDataInput):void {
      _docRef = input.readUTF();
      _name = input.readUTF();
      _path = input.readUTF();
      _lifeCycleState = input.readUTF();
      _type=input.readUTF();
      _icon=input.readUTF();
      _iconExpanded=input.readUTF();   
      _isFolder=input.readBoolean();
      _data = input.readObject();
      _dirty = input.readObject();
      _isDirty= input.readBoolean();
    }

    public function writeExternal(output:IDataOutput):void {
      output.writeUTF(_docRef);
      output.writeUTF(_name);
      output.writeUTF(_path);
      output.writeUTF(_lifeCycleState);
      output.writeUTF(_type);
      output.writeUTF(_icon);
      output.writeUTF(_iconExpanded);
      output.writeBoolean(_isFolder);
      output.writeObject(_dirty);
    }

    public function get id():String
    {
      return _docRef;
    }

    public function get name():String
    {
      return _name;
    }

    public function get doctype():String
    {
      return _type;
    }

    public function get icon():String
    {
      return _icon;
    }

    public function get iconExpanded():String
    {
      return _iconExpanded;
    }
        
    public function get contentdata():Object
    {
      return _data;
    }

    public function set name(value:String):void
    {
      _name= value;
    }

    public function getTitle():String
    {
      var title:String=_data.dublincore.title;
      if (title=="" || title==null) {
         title=_name;
      }
      if (_path=="/") {
         title="/";
      }
      if (title=="" || title==null) {
         title=_docRef;
      }
      return title;
    }

    public function setTitle(value:String):void
    {
      _data.dublincore.title=value;
      _dirty['dublincore:title']=value;
      markDirty();
    }

    public function getProperty(schemaName:String, fieldName:String):Object
    {
      return _data[schemaName][fieldName];
    }

    public function setProperty(schemaName:String, fieldName:String, value:String):void
    {
      _data[schemaName][fieldName]=value;
      _dirty[schemaName+":"+fieldName]=value;
      markDirty();
    }

    public function isDirty(schemaName:String, fieldName:String):Boolean {
        var key:String = schemaName+":"+fieldName;        
	return _dirty[key]!=null;
    }

    public function isFolder():Boolean
    {
      return _isFolder;
    }

    public function getSchemas():Array
    {
      var schemas:Array = new Array();
      var schemaName:Object;
      for (schemaName in _data)
      {
      schemas.push(schemaName);
      }
      return schemas;
    }

    public function getSchema(schemaName:String):Object
    {
      return _data[schemaName];
    }

    public function getFieldNames(schemaName:String):Array
    {
      var fieldNames:Array = new Array();
      var fieldName:Object;
      for (fieldName in _data[schemaName])
      {
      fieldNames.push(fieldName);
      }
      return fieldNames;
    }

    public function get path():String{
       return _path;
    }

    public function get dirtyFields():Object{
        return _dirty;
    }

    protected function markDirty():void {
       _isDirty=true;
       dispatchEvent(new Event("dirtyDocument"));
    }

    [Bindable(event="dirtyDocument")]
    public function get dirty():Boolean {
        return _isDirty;
    }
  }

}
