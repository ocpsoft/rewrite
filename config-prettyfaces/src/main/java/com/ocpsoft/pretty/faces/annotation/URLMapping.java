/*
 * Copyright 2010 Lincoln Baxter, III
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ocpsoft.pretty.faces.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Annotation representing a single PrettyFaces mapping that maps URLs to Faces
 * Views.
 * </p>
 * <p>
 * All {@link URLAction} and {@link URLQueryParameter} annotations placed on
 * methods or fields of the annotated class will automatically refer to this
 * mapping.
 * </p>
 * 
 * @author Christian Kaltepoth
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface URLMapping
{

   /**
    * Each {@link URLMapping} must specify a unique id in order to participate
    * in PrettyFaces navigation.
    */
   String id() default "";

   /**
    * <p>
    * Specify the pattern for which this URL will be matched. This element is
    * required.
    * </p>
    * <p>
    * Any EL expressions #{someBean.paramName} found within the pattern will be
    * processed as value injections. The URL will be parsed and the value found
    * at the location of the EL expression will be injected into the location
    * specified in that EL expression. Note: EL expressions will not match over
    * the ‘/’ character.
    * </p>
    * <p>
    * The pattern itself is compiled parsed as a regular expression, meaning
    * that the actual URL matching can be as simple or as complex as desired.
    * </p>
    */
   String pattern();

   /**
    * <p>
    * Specify an optional parent {@link URLMapping#id()} from which this mapping
    * will inherit the base pattern, and path parameter validators.
    * </p>
    * 
    * <strong>For example:</strong>
    * 
    * <pre>
    * &lt;url-mapping id=&quot;store&quot;&gt; <br/>   &lt;pattern value=&quot;/store/&quot; /&gt; <br/>   &lt;~-- Result: /store/ --&gt;<br/>   &lt;view-id&gt;/faces/shop/store.jsf&lt;/view-id&gt;<br/>&lt;/url-mapping&gt;<br/><br/>&lt;url-mapping parentId=&quot;store&quot; id=&quot;category&quot;&gt; <br/>   &lt;pattern value=&quot;/#{category}&quot; /&gt; <br/>   &lt;~-- Result: /store/#{category} --&gt;<br/>   &lt;view-id&gt;/faces/shop/category.jsf&lt;/view-id&gt;<br/>&lt;/url-mapping&gt;<br/><br/>&lt;url-mapping parentId=&quot;category&quot; id=&quot;item&quot;&gt; <br/>   &lt;pattern value=&quot;/#{item}&quot; /&gt; <br/>   &lt;~-- Result: /store/#{category}/#{item} --&gt;<br/>   &lt;view-id&gt;/faces/shop/item.jsf&lt;/view-id&gt;<br/>&lt;/url-mapping&gt;<br/><br/>&lt;url-mapping parentId=&quot;category&quot; id=&quot;sales&quot;&gt; <br/>   &lt;pattern value=&quot;/sales&quot; /&gt; <br/>   &lt;~-- Result: /store/#{category}/sales --&gt;<br/>   &lt;view-id&gt;/faces/shop/sales.jsf&lt;/view-id&gt;<br/>&lt;/url-mapping&gt;
    * </pre>
    * 
    * 
    * 
    */
   String parentId() default "";

   /**
    * <p>
    * Specify the JSF ViewId displayed by this mapping, by either calling an EL
    * Method (must return an object for which the toString() method will return
    * the view Id) or by returning a literal String value. This element is
    * required.
    * </p>
    * <p>
    * The ViewId may be any resource located within the current Servlet Context:
    * E.g. PrettyFaces can also forward to a non-Faces servlet.
    * </p>
    */
   String viewId();

   /**
    * <p>
    * Specify any number of pattern validators for this mapping. Validators may
    * be attached to individual parameters in each dynamic URL.
    * </p>
    * <p>
    * Please not that the <code>index</code> property of the
    * {@link URLValidator} annotations must be set to identify the path
    * parameter you want the validation to refer to.
    * </p>
    */
   URLValidator[] validation() default {};

   /**
    * Enable or disable outbound URL rewriting for this mapping (default: 'true'
    * / enabled.) If enabled, any links matching the viewId specified will be
    * rewritten (if possible) using parameters mapping to named path parameters
    * specified in the pattern.
    */
   boolean outbound() default true;

   /**
    * <p>
    * The name that can be used to access instances of this bean in EL
    * expressions. Setting this attribute is equivalent to adding a
    * {@link URLBeanName} annotation to the class.
    * </p>
    * 
    * @see URLBeanName
    */
   String beanName() default "";

   /**
    * <p>
    * Optional boolean (default true), if set to <code>false</code>, path
    * parameters will not be injected on form postbacks.
    * </p>
    */
   boolean onPostback() default true;

}
