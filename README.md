# SimpleBackground-Eclipse

This Eclipse Plugin allows to configure the background in Eclipse.

Under `Window`>`Preferences`>`SimpleBackground`, the following options are available:
- `Background image`: This option allows to set a background image.
- `Background alpha`: This option allows to customize the transparency of the main Eclipse Window.
- `Background image alpha`: This option allows to customize the transparency of the background image.
This option is ignored if no background image is set.

## Setup

### Installation
* Select `Help`>`Install New Software`<br/>
![image](https://user-images.githubusercontent.com/34687786/123937084-196fa100-d996-11eb-8105-108a32d94865.png)
* Click on `Add Software Site`<br/>
![image](https://user-images.githubusercontent.com/34687786/123937282-4b810300-d996-11eb-8d2a-cdc8805751dc.png)
* In the dialog, enter a name and `https://raw.githubusercontent.com/danthe1st/eclipse-update-site/master/` as the URL<br/>
![image](https://user-images.githubusercontent.com/34687786/123937393-66ec0e00-d996-11eb-88ad-a0181644ae6f.png)
* Select the created Software Site under `Work With` and unselect `Group Items by category` <br/>
* Select `SimpleBackground` and click on `Next`
* Complete the installation process

### Development setup
- Install Eclipse
- Make sure that [Eclipse PDE](https://marketplace.eclipse.org/content/eclipse-pde-plug-development-environment) is installed. It can be installed from the Eclipse Marketplace here:<br/>[![Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client](https://marketplace.eclipse.org/sites/all/themes/solstice/public/images/marketplace/btn-install.svg)](http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=2234530 "Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client")
- Make sure the m2e plugin is installed. It can be installed
- Clone this repository
- Import it as a Maven project including the module `simplebackground-main` (`io.github.danthe1st.simple-background`)
- Open the file `plugin.xml` with the `Plug-in Manifest Editor` in the `simplebackground-main` (`io.github.danthe1st.simple-background`) module and click on the run button on the top right in order to start Eclipse with this plugin.<br/>
![image](https://user-images.githubusercontent.com/34687786/123833918-25605200-d907-11eb-8b07-2a3954218f32.png)