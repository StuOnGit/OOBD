package utility;

import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.control.TextInputControl;

// Classe per evitare codice boilerplate per la generazione dei Validators (per textField)
public class Validators{

    public RequiredFieldValidator createRequiredValidator(String message){
        RequiredFieldValidator v = new RequiredFieldValidator();
        v.setMessage(message);
        FontAwesomeIcon f = new FontAwesomeIcon();
        f.setIconName("EXCLAMATION_TRIANGLE");
        v.setIcon(f);
        return v;
    }

    public ValidatorBase createEmailValidator(String m){
        ValidatorBase v = new ValidatorBase() {
            @Override
            protected void eval() {
                if (srcControl.get() instanceof TextInputControl) {
                    evalTextInputField();
                }
            }

            private void evalTextInputField() {
                TextInputControl textField = (TextInputControl) srcControl.get();
                if (textField.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$") || textField.getText().isEmpty()) {
                    hasErrors.set(false);
                } else {
                    hasErrors.set(true);
                }
            }
        };

        v.setMessage(m);
        FontAwesomeIcon f = new FontAwesomeIcon();
        f.setIconName("EXCLAMATION_TRIANGLE");
        v.setIcon(f);
        return v;
    }


    public ValidatorBase createPassportValidator(String m){
        ValidatorBase v = new ValidatorBase() {
            @Override
            protected void eval() {
                if (srcControl.get() instanceof TextInputControl) {
                    evalTextInputField();
                }
            }

            private void evalTextInputField() {
                TextInputControl textField = (TextInputControl) srcControl.get();
                if (textField.getText().matches("^(?!^0+$)[a-zA-Z0-9]{6,9}$")) {
                    hasErrors.set(false);
                } else {
                    hasErrors.set(true);
                }
            }
        };

        v.setMessage(m);
        FontAwesomeIcon f = new FontAwesomeIcon();
        f.setIconName("EXCLAMATION_TRIANGLE");
        v.setIcon(f);
        return v;
    }

    public ValidatorBase createCfValidator(String m){
        ValidatorBase v = new ValidatorBase() {
            @Override
            protected void eval() {
                if (srcControl.get() instanceof TextInputControl) {
                    evalTextInputField();
                }
            }

            private void evalTextInputField() {
                TextInputControl textField = (TextInputControl) srcControl.get();
                if (textField.getText().matches("^[a-zA-Z]{6}[0-9]{2}[abcdehlmprstABCDEHLMPRST]{1}[0-9]{2}([a-zA-Z]{1}[0-9]{3})[a-zA-Z]{1}$")) {
                    hasErrors.set(false);
                } else {
                    hasErrors.set(true);
                }
            }
        };

        v.setMessage(m);
        FontAwesomeIcon f = new FontAwesomeIcon();
        f.setIconName("EXCLAMATION_TRIANGLE");
        v.setIcon(f);
        return v;
    }

    public ValidatorBase createPatentValidator(String m){
        ValidatorBase v = new ValidatorBase() {
            @Override
            protected void eval() {
                if (srcControl.get() instanceof TextInputControl) {
                    evalTextInputField();
                }
            }

            private void evalTextInputField() {
                TextInputControl textField = (TextInputControl) srcControl.get();
                if (textField.getText().length() == 10) {
                    hasErrors.set(false);
                } else {
                    hasErrors.set(true);
                }
            }
        };

        v.setMessage(m);
        FontAwesomeIcon f = new FontAwesomeIcon();
        f.setIconName("EXCLAMATION_TRIANGLE");
        v.setIcon(f);
        return v;
    }

    public ValidatorBase createIdValidator(String m){
        ValidatorBase v = new ValidatorBase() {
            @Override
            protected void eval() {
                if (srcControl.get() instanceof TextInputControl) {
                    evalTextInputField();
                }
            }

            private void evalTextInputField() {
                TextInputControl textField = (TextInputControl) srcControl.get();
                if (textField.getText().length() == 9) {
                    hasErrors.set(false);
                } else {
                    hasErrors.set(true);
                }
            }
        };

        v.setMessage(m);
        FontAwesomeIcon f = new FontAwesomeIcon();
        f.setIconName("EXCLAMATION_TRIANGLE");
        v.setIcon(f);
        return v;
    }
}
