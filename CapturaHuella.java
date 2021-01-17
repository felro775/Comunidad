/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Formulario;

//*** INICIO - LIBRERIAS DE LECTOR DE HUELLAS DIGITALES
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;

import BD.ConexionBD;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
//*** FIN - LIBRERIAS DE LECTOR DE HUELLAS DIGITALES


/**
 *
 * @author Admin
 */



public class CapturaHuella extends javax.swing.JFrame {

    /**
     * Creates new form CapturaHuella
     */
    public CapturaHuella() {
        
        //*** INICIO - OPCIONES LECTOR DE HUELLA DIGITAL
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());            
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Imposible modificar el tema visual","Lookandfeel invalido",
            JOptionPane.ERROR_MESSAGE);
        }       
        //*** FIN - OPCIONES LECTOR DE HUELLA DIGITAL
        
        initComponents();               
    }

        //*** INICIO - VARIABLES DE CAPTURA DE HUELLA DIGITAL
        private DPFPCapture Lector = DPFPGlobal.getCaptureFactory().createCapture();
        private DPFPEnrollment Reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment();
        private DPFPVerification Verificador = DPFPGlobal.getVerificationFactory().createVerification();
        private DPFPTemplate template;
        public static String TEMPLATE_PROPERTY = "template";
        
        public DPFPFeatureSet featuresinscripcion;
        public DPFPFeatureSet featuresverificacion;              
        //FIN - VARIABLES DE CAPTURA DE HUELLA DIGITAL
    
        
        //INICIO - FUNCIONES DE INFORMACION DE HUELLA DIGITAL
        protected void Iniciar()
        {
            Lector.addDataListener(new DPFPDataAdapter() {
                @Override
                public void dataAcquired(final DPFPDataEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            EnviarTexto("La Huella Digital ha sido Capturada");
                            ProcesarCaptura(e.getSample());
                        }
                    });
                }
            });
            Lector.addReaderStatusListener(new DPFPReaderStatusAdapter() {
                @Override
                public void readerConnected(final DPFPReaderStatusEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            EnviarTexto("El Sensor de Huella esta Activado o Conectado");
                        }
                });
            }

            @Override
            public void readerDisconnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        EnviarTexto("El Sensor de Huella esta Desactivado o Desconectado");
                    }
                });
            }                      
        });
            
        Lector.addSensorListener(new DPFPSensorAdapter() {
            @Override
            public void fingerTouched(final DPFPSensorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        EnviarTexto("El dedo ha sido colocado sobre el Lector de Huella");
                    }
                });
            }

            @Override
            public void fingerGone(final DPFPSensorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        EnviarTexto("El dedo ha sido quitado sobre el Lector de Huella");
                    }
                });
            }
        });
        Lector.addErrorListener(new DPFPErrorAdapter() {
            public void errorReader(final DPFPErrorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        EnviarTexto("ERROR: "+e.getError());
                    }
                });
            }
        });
        //*** FIN - FUNCIONES DE INFORMACION DE HUELLA DIGITAL
                
    }
            
    //*** INICIO - FUNCIONES DE CAPTURA DE HUELLA DIGITAL    
    public DPFPFeatureSet extraerCaracteristicas(DPFPSample sample, DPFPDataPurpose purpose) {
        DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
        try {
            return extractor.createFeatureSet(sample, purpose);
        } catch (DPFPImageQualityException e) {
            return null;
        }
    }
    
    public Image CrearImagenHuella(DPFPSample sample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }
    
    public void DibujarHuella(Image image) {
        lblImagenHuella.setIcon(new ImageIcon(
                image.getScaledInstance(lblImagenHuella.getWidth(),
                        lblImagenHuella.getHeight(),
                        Image.SCALE_DEFAULT)));
        repaint();
        /*
        try {
            identificarHuella();
            stop();
            start();
            Reclutador.clear();
        } catch (IOException ex) {
        }
        */
    }
    
    public void EstadoHuellas() {
        EnviarTexto("Muestra de Huellas Necesarias para Guardar Template");
        Reclutador.getFeaturesNeeded();
    }

    public void start() {
        Lector.startCapture();
        EnviarTexto("Utilizadno el Lector de Huella Digital");
    }

    public void stop() {
        Lector.stopCapture();
        EnviarTexto("No se encuentra en uso el Lector de Huella Digital");
    }

    public DPFPTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DPFPTemplate template) {
        DPFPTemplate old = this.template;
        this.template = template;
        firePropertyChange(TEMPLATE_PROPERTY, old, template);
    }
    
    public void ProcesarCaptura(DPFPSample sample) {
        //Procesar la muestra de la huella y crear un conjunto de caracteristicas con el proposito de inscripcion
        featuresinscripcion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        //Procesar la muestra de la huella y crear un conjunto de caracteristicas con el proposito de verificacion
        featuresverificacion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        //Comprobar la calidad de la muestra de la huella y lo añadea su reclutador si es bueno
        if (featuresinscripcion != null) {
            try {
                EnviarTexto("Las Caracteristicas de la Huella han sido creada");
                Reclutador.addFeatures(featuresinscripcion); //Agregar las caracteristicas de la huella a la plantilla

                //Dibuja la huella dactilar capturada
                Image image = CrearImagenHuella(sample);
                DibujarHuella(image);
                
                btnVerificar.setEnabled(true);
                btnIdentificar.setEnabled(true);

            } catch (DPFPImageQualityException ex) {
                System.err.println("Error: " + ex.getMessage());;
            } finally {
                EstadoHuellas();
                //Comprueba si la plantilla se ha creado
                switch (Reclutador.getTemplateStatus()) {
                    case TEMPLATE_STATUS_READY: // informe de exito y detiene la captura de huella digital
                        stop();
                        setTemplate(Reclutador.getTemplate());
                        EnviarTexto("La Plantilla de la Huella ha sido creada, ya puede Verificarla o Identificarla");
                        btnIdentificar.setEnabled(false);
                        btnVerificar.setEnabled(false);
                        btnGuardar.setEnabled(true);
                        btnGuardar.grabFocus();                                           
                        break;
                    case TEMPLATE_STATUS_FAILED: // informe de fallas y reiniciar la captura de huellas
                        Reclutador.clear();
                        stop();
                        EstadoHuellas();
                        setTemplate(null);
                        //MENSAJE DE DIALOGO DE ERROR
                        JOptionPane.showMessageDialog(CapturaHuella.this, "La Plantilla de la Huella no pudo ser creada, repita el proceso");
                        start();
                        break;
                }

            }
        }
    }      
    
    public void EnviarTexto(String string){
        txtArea.append(string + "\n");
    }
    
    ConexionBD cn = new ConexionBD();
    
    // FUNCION PARA GUARDAR LA HUELLA DIGITAL
    public void guardarHuella() throws SQLException{
        
        //OBTENENOS LOS DATOS DEL TEMPLATE DE LA HUELLA ACTUAL
        ByteArrayInputStream datosHuella = new ByteArrayInputStream(template.serialize());
        Integer tamañoHuella = template.serialize().length;
        
        //PREGUNTAMOS EL NOMBRE DE LA PERSONA A LA QUE CORRESPONDE LA HUELLA
        String nombre = JOptionPane.showInputDialog("Nombre : ");
        try{
            Connection c = cn.conectar();            
            PreparedStatement guardarStmt = c. prepareStatement ("INSERT INTO somhue(hueNombre, hueHuella) values(?,?)");
            guardarStmt.setString(1,nombre);
            guardarStmt.setBinaryStream(2,datosHuella,tamañoHuella);
        //EJECUTAMOS LA SENTENCIA DE CONSULTA SQL  
            guardarStmt.execute();
            guardarStmt.close();
            JOptionPane.showMessageDialog(null,"Huella Guardada Correctamente...");
            cn.desconectar();
            btnGuardar.setEnabled(false);
            btnVerificar.grabFocus();
        }catch (SQLException ex){
            System.err.println("Error al guardar los datos de la huella");            
        }finally {
            cn.desconectar();
        }   
    }
    
    //FUNCION PARA VERIFICAR LA HUELLLA DIGITAL
    public void verificarHuella(String nom){
    
        try{
            Connection c = cn.conectar();
            // OBTIENE LA PLANTILLA CORRESPONDIENTE A LA PERSONA INDICADA
            PreparedStatement verificarStmt = c. prepareStatement ("SELECT hueHuella FROM somhue WHERE hueNombre=?");
            verificarStmt.setString(1,nom);
            ResultSet rs = verificarStmt.executeQuery();
            
            if (rs.next()){
                byte templateBuffer[] = rs.getBytes("hueHuella");
                // CREAR UNA NUEVA PLANTILLA A PARTIR DE LA GUARDAD EN LA BASE DE DATOS
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                // ENVIA LA PLANTILLA CREADA AL OBJETO CONTENEDOR DEL TEMPLATE DECOMPONENTE
                setTemplate(referenceTemplate);
                // PLANTILLA GUARDAD AL USUARIO ESPEPCIFICO EN LA BASE DE DATOS
                DPFPVerificationResult result = Verificador.verify(featuresinscripcion, getTemplate());
                
                // COMPARA LAS PLANTILLA (ACTUAL VS BD)
                if (result.isVerified()){
                    JOptionPane.showMessageDialog(null, "La huella capturada coincide con la de "+nom, 
                            "Verificacion de huella",JOptionPane.ERROR_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(null, "No corresponde la huella con "+nom, 
                            "Verificacion de huella",JOptionPane.ERROR_MESSAGE);
                }
            }
            else{
                JOptionPane.showMessageDialog(null, "NO EXISTE UN REGISTRO DE HUELLA PARA  "+nom, 
                            "Verificacion de huella",JOptionPane.ERROR_MESSAGE);    
            }                 
        }catch (SQLException e){
            System.err.println("Error al verificar los datos de la huella");
        }finally {
            cn.desconectar();
        }        
    }
    
    //FUNCION PARA IDENTIFICAR LA HUELLA DIGITAL
    public void identificarHuella() throws SQLException {    
        try{
            Connection c = cn.conectar();
            // OBTIENE LA PLANTILLA CORRESPONDIENTE A LA PERSONA INDICADA
            PreparedStatement identificarStmt = c. prepareStatement ("SELECT hueNombre,hueHuella FROM somhue");            
            ResultSet rs =identificarStmt.executeQuery();
            // SI SE ENCUENTRA EL NOMBRE EN LA BASE DE DATOS
            while(rs.next()){
                // LEE LA PLANTILLA DE LA BASE DE DATOS
                byte templateBuffer[] = rs.getBytes("hueHuella");
                String nombre = rs.getString("hueNombre");
                // CREAR UNA NUEVA PLANTILLA A PARTIR DE LA GUARDAD EN LA BASE DE DATOS
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                // ENVIA LA PLANTILLA CREADA AL OBJETO CONTENEDOR DEL TEMPLATE DECOMPONENTE
                setTemplate(referenceTemplate);
                // PLANTILLA GUARDAD AL USUARIO ESPEPCIFICO EN LA BASE DE DATOS
                DPFPVerificationResult result = Verificador.verify(featuresverificacion, getTemplate());
                
                if (result.isVerified()){                   
                    JOptionPane.showMessageDialog(null, "La huella capturada es de "+nombre, 
                            "Identificacion de huella",JOptionPane.ERROR_MESSAGE);
                    return;
                }                
            }
            JOptionPane.showMessageDialog(null, "No EXISTE registro que coincida con la Huella",
                    "Identificacion de huella",JOptionPane.ERROR_MESSAGE);
            setTemplate(null);
        }catch (SQLException e){
            System.err.println("Error al verificar los datos de la huella");
        }finally {
            cn.desconectar();
        }        
    }        
    //*** FIN - FUNCIONES DE CAPTURA DE HUELLA DIGITAL
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblImagenHuella = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnVerificar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnIdentificar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Huellas Digitales", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 0, 18))); // NOI18N

        lblImagenHuella.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(lblImagenHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblImagenHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Opciones Huellas Digitales", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 0, 18))); // NOI18N

        btnVerificar.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        btnVerificar.setText("VERIFICAR");
        btnVerificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerificarActionPerformed(evt);
            }
        });

        btnGuardar.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        btnGuardar.setText("GUARDAR");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnIdentificar.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        btnIdentificar.setText("IDENTIFICAR");
        btnIdentificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIdentificarActionPerformed(evt);
            }
        });

        btnSalir.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        btnSalir.setText("SALIR");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(btnVerificar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addComponent(btnGuardar)
                .addGap(75, 75, 75)
                .addComponent(btnIdentificar)
                .addGap(69, 69, 69)
                .addComponent(btnSalir)
                .addGap(62, 62, 62))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVerificar)
                    .addComponent(btnGuardar)
                    .addComponent(btnIdentificar)
                    .addComponent(btnSalir))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        txtArea.setColumns(20);
        txtArea.setRows(5);
        jScrollPane1.setViewportView(txtArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    //*** INICIO DE FUNCIONES DE BOTONES E INICIO DE FORMULARIO    
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
        System.exit(0);
    }                                        
    
    private void btnVerificarActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO add your handling code here:
        String nombre = JOptionPane.showInputDialog("Nombre a verificar: ");
        verificarHuella(nombre);
        Reclutador.clear();        
    }                                            

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:
        try{
            guardarHuella();
            Reclutador.clear();
            lblImagenHuella.setIcon(null);
            start();
        }catch (SQLException ex){
            Logger.getLogger(CapturaHuella.class.getName()).log(Level.SEVERE, null, ex);
        }
    }                                          

    private void btnIdentificarActionPerformed(java.awt.event.ActionEvent evt) {                                               
        // TODO add your handling code here:
        try{
            identificarHuella();
            Reclutador.clear();            
        }catch (SQLException ex){
            Logger.getLogger(CapturaHuella.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }                                              

    private void formWindowOpened(java.awt.event.WindowEvent evt) {                                  
        // TODO add your handling code here:
        Iniciar();
        start();
        EstadoHuellas();
        btnGuardar.setEnabled(false);
        btnIdentificar.setEnabled(false);
        btnVerificar.setEnabled(false);
        btnSalir.grabFocus();
    }                                 

    private void formWindowClosed(java.awt.event.WindowEvent evt) {                                  
        // TODO add your handling code here:
        stop();
    }                                 

    //*** FIN DE FUNCIONES DE BOTONES E INICIO DE FORMULARIO
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CapturaHuella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CapturaHuella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CapturaHuella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CapturaHuella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CapturaHuella().setVisible(true);
            }
        });
        
        
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnIdentificar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnVerificar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblImagenHuella;
    private javax.swing.JTextArea txtArea;
    // End of variables declaration                   

    
}
