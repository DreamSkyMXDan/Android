# Android
This includes a couple of models that are used by my apps that are sold to customers.</br></br></br></br>
1. Signature</br></br>
Signature class inherites from relativelayout. It can be instantiated in display mode and user interactive mode. First of all, a signature box will be displayed on the form in display mode and when it is clicked, the box will pop up and change into a dialog where the user can draw signature in the signatureview interactively. In display mode, user cannot draw anything in the signatureview. </br></br>
SignatureView class inherites from imageview where user could draw signatures. In the signature layout, signatureview is a subview of signature. </br></br>
A signature.xml should be created to serve as the layout of the signature and the signatureview should be embedded in the layout.</br></br>
To use this module, simply instantiate a new signature object like this View view = new Signature(getActivity(), labelText, SignatureView.DISPLAY_ONLY); then add this view as a subview of your rootview like so, rootview.addView(view).
