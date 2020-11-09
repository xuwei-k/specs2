package org.specs2
package specification
package create

import control.ImplicitParameters.ImplicitParam
import core._
import form._
import text.NotNullStrings._
import execute._
import control.Exceptions._
import scala.reflect.Selectable.reflectiveSelectable
import FormsBuilder.{given _, _}

/**
 * Factory for creating Form fragments
 */
trait FormFragmentFactory:
  def FormFragment(form: =>Form): Fragment
  def FormFragment(aForm: =>{ def form: Form })(using p: ImplicitParam): Fragment

/**
 * Default implementation for the FormFragment Factory
 */
trait DefaultFormFragmentFactory extends FormFragmentFactory:
  def FormFragment(aForm: =>HasForm)(using p: ImplicitParam): Fragment = addForm(aForm.form)

  def FormFragment(aForm: =>Form): Fragment = addForm(aForm)

  private def addForm(aForm: =>Form): Fragment =
    lazy val form: Form =
      tryOr(aForm.executeForm){ t =>
        Form("Initialisation error").tr(PropCell(prop("", t.getMessage.notNull, (_: String, _: String) => Error(t)).apply("message")))
      }

    Fragment(FormDescription(() => form), Execution.result(form.result.getOrElse(Success(""))))
object DefaultFormFragmentFactory extends DefaultFormFragmentFactory


trait FormFragmentsFactory:
  protected def formFragmentFactory: FormFragmentFactory = DefaultFormFragmentFactory
