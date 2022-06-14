package psp42022;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class ClienteFTPBasico extends JFrame
{
	private static final long serialVersionUID = 1L;
	// Campos de la cabecera parte superior
	static JTextField txtServidor = new JTextField();
	static JTextField txtUsuario = new JTextField();
	static JTextField txtDirectorioRaiz = new JTextField();
	// Campos de mensajes parte inferior
	private static JTextField txtArbolDirectoriosConstruido = new JTextField();
	private static JTextField txtActualizarDirectorio = new JTextField();
	private static JTextField txtInformaci�n = new JTextField();
	// btnes
	JButton btnCargarArchivo = new JButton("Subir fichero");
	JButton btnDescargar = new JButton("Descargar fichero");
	JButton btnBorrarArchivo = new JButton("Eliminar fichero");
	JButton btnCreaCarpeta = new JButton("Crear carpeta");
	JButton btnBorrarCarpeta = new JButton("Eliminar carpeta");
	JButton btnSalir = new JButton("Salir");
	JButton btnVolver = new JButton("Volver");
	JButton btnRenombrarDirectorio = new JButton("Renombrar Directorio");
	JButton btnRenombrarArchivo = new JButton("Renombrar Archivo");
	// Lista para los datos del directorio
	static JList<String> listaDirec = new JList<String>();
	// contenedor
	private final Container ventana = getContentPane();
	// Datos del servidor FTP - Servidor local
	static FTPClient cliente = new FTPClient();// cliente FTP
	String servidor = "127.0.0.1";
	String user = "miguervz";
	String pasw = "miguervz";
	boolean login;
	static String direcInicial = "/";
	// para saber el directorio y fichero seleccionado
	static String direcSelec = direcInicial;
	static String ficheroSelec = "";

	public static void main(String[] args) throws IOException
	{
		new ClienteFTPBasico();
	} // final del main

	public ClienteFTPBasico() throws IOException
	{
		super("CLIENTE B�SICO FTP");
		// para ver los comandos que se originan
		cliente.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		cliente.connect(servidor); // conexi�n al servidor
		cliente.enterLocalPassiveMode();
		login = cliente.login(user, pasw);
		// Se establece el directorio de trabajo actual
		cliente.changeWorkingDirectory(direcInicial);
		// Obteniendo ficheros y directorios del directorio actual
		FTPFile[] files = cliente.listFiles();
		llenarLista(files, direcInicial);
		// Construyendo la lista de ficheros y directorios
		// del directorio de trabajo actual
		// preparar campos de pantalla
		
		txtServidor.setText("Servidor FTP: " + servidor);
		txtUsuario.setText("Usuario: " + user);		
		txtActualizarDirectorio.setText("<<   Selecci�n Actual  >>");
		txtDirectorioRaiz.setText("DIRECTORIO RAIZ: " + direcInicial );
		txtArbolDirectoriosConstruido.setText("<<    �RBOL DE DIRECTORIOS   >>                                                                          ");
		txtInformaci�n.setText("                                                                                                                                           ");
		
		// Preparaci�n de la lista
		// se configura el tipo de selecci�n para que solo se pueda
		// seleccionar un elemento de la lista

		listaDirec.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// barra de desplazamiento para la lista
		JScrollPane barraDesplazamiento = new JScrollPane(listaDirec);
		barraDesplazamiento.setPreferredSize(new Dimension(335, 420));
		barraDesplazamiento.setBounds(new Rectangle(5, 65, 335, 420));
		ventana.add(barraDesplazamiento);
		ventana.add(txtServidor);
		ventana.add(txtDirectorioRaiz);		
		ventana.add(txtActualizarDirectorio);
		ventana.add(txtArbolDirectoriosConstruido);
		ventana.add(txtInformaci�n);
		ventana.add(btnVolver);		
		ventana.add(btnCargarArchivo);
		ventana.add(btnCreaCarpeta);
		ventana.add(btnBorrarCarpeta);
		ventana.add(btnBorrarArchivo);
		ventana.add(btnRenombrarDirectorio);
		ventana.add(btnRenombrarArchivo);		
		ventana.add(btnDescargar);			
		ventana.add(btnSalir);
		ventana.setLayout(null);
		// se a�aden el resto de los campos de pantalla
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		setSize(510, 645);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);

		// Acciones al pulsar en la lista o en los btnes
		listaDirec.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent lse)
			{
				String fic = "";
				if (lse.getValueIsAdjusting())
				{
					ficheroSelec = "";
					// elemento que se ha seleccionado de la lista
					fic = listaDirec.getSelectedValue().toString();
					// Se trata de un fichero
					ficheroSelec = direcSelec;
					// txtArbolDirectoriosConstruido.setText(fic);
					ficheroSelec = fic;// nos quedamos con el nombre
					txtActualizarDirectorio.setText("DIRECTORIO: " + direcSelec + ficheroSelec);
					String[] sel = ficheroSelec.split(" ");
					// Si contiene espacios el archivo seleccionado, capturamos el archivo CON un
					// split()
					if (ficheroSelec.contains(" "))
					{
						txtActualizarDirectorio.setText(direcSelec + sel[1]);
					}
					// Si el archivo seleccionado no contiene espacios capturamos SIN split()
					else
					{
						ficheroSelec.trim();
						txtActualizarDirectorio.setText(direcSelec + ficheroSelec);
					}
				}
			}
		});

		// Acci�n DOBLE CLIC
		listaDirec.addMouseListener(new MouseAdapter()
		{
			@SuppressWarnings("unchecked")
			public void mouseClicked(MouseEvent evt)
			{
				if (ficheroSelec.contains("(DIR)")) 
				{
					listaDirec = (JList<String>) evt.getSource();
					if (evt.getClickCount() == 2)
					{

						
						String nombreDir = ficheroSelec;
						String[] name = nombreDir.split(" ");
						// quita blancos a derecha y a izquierda
						String nombre = name[1];

						txtArbolDirectoriosConstruido.setText(direcSelec + nombre + "/");
						txtActualizarDirectorio.setText(nombre);
						try
						{
							direcSelec = direcSelec + nombre + "/";
							// directorio de trabajo actual
							cliente.changeWorkingDirectory(direcSelec);
							FTPFile[] ff2 = null;
							// obtener ficheros del directorio actual
							ff2 = cliente.listFiles();
							// llenar la lista
							llenarLista(ff2, direcSelec);
						} catch (IOException ex)
						{
							ex.printStackTrace();
						}

						int index = listaDirec.locationToIndex(evt.getPoint());
						System.out.println("index: " + (index + 1));
					}
				}
				
				
			}

		});

		btnSalir.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					cliente.disconnect();
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		btnCreaCarpeta.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String nombreCarpeta = JOptionPane.showInputDialog(null, "Introduce el nombre del directorio",
						"carpeta");
				if (!(nombreCarpeta == null))
				{
					String directorio = direcSelec;
					if (!direcSelec.equals("/"))
						directorio = directorio + "/";
					// nombre del directorio a crear
					directorio += nombreCarpeta.trim();
					// quita blancos a derecha y a izquierda
					try
					{
						if (cliente.makeDirectory(directorio))
						{
							String m = nombreCarpeta.trim() + " => Se ha creado correctamente ...";
							JOptionPane.showMessageDialog(null, m);
							txtInformaci�n.setText(m);
							// directorio de trabajo actual
							cliente.changeWorkingDirectory(direcSelec);
							FTPFile[] ff2 = null;
							// obtener ficheros del directorio actual
							ff2 = cliente.listFiles();
							// llenar la lista
							llenarLista(ff2, direcSelec);
						} else
							JOptionPane.showMessageDialog(null, nombreCarpeta.trim() + " => No se ha podido crear ...");
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				} // final del if
			}
		}); // final del bot�n CreaDir
		btnRenombrarDirectorio.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{

				if (ficheroSelec.contains("(DIR)"))
				{
					String[] renFich = ficheroSelec.split(" ");
					String directorio = JOptionPane.showInputDialog(null,
							"Introduce el nombre ACTUAL del directorio a renombrar", renFich[1]);
					String nombreCarpeta = JOptionPane.showInputDialog(null, "Introduce el NUEVO nombre del directorio",
							renFich[1]);
					if (!(nombreCarpeta == null))
					{
						// String directorio = direcSelec;
						if (!direcSelec.equals("/"))

							// nombre del directorio a renombrar
							directorio = direcSelec.trim() + ficheroSelec.trim(); // quita blancos a derecha y a
																					// izquierda
						System.out.println(directorio);
						try
						{
							if (cliente.isAvailable())
							{
								cliente.rename(directorio, nombreCarpeta);
								String m = directorio.trim() + " => Se ha modificado correctamente ...";
								JOptionPane.showMessageDialog(null, m);
								txtInformaci�n.setText(m);
								// directorio de trabajo actual
								cliente.changeWorkingDirectory(direcSelec);
								FTPFile[] ff2 = null;
								// obtener ficheros del directorio actual
								ff2 = cliente.listFiles();
								// llenar la lista
								llenarLista(ff2, direcSelec);
							} else
								JOptionPane.showMessageDialog(null,
										nombreCarpeta.trim() + " => No se ha podido renombrar ...");
						} catch (IOException e1)
						{
							e1.printStackTrace();
						}
					}
				} else
				{

					JOptionPane.showMessageDialog(null, ficheroSelec + "--> No es una CARPETA");

				}
			}
		});// Bot�n renombrar directorio
		btnRenombrarArchivo.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{

				if (!ficheroSelec.contains("(DIR)"))
				{
					String directorio = JOptionPane.showInputDialog(null,
							"Introduce el nombre ACTUAL del archivo a renombrar", ficheroSelec);
					String nombreCarpeta = JOptionPane.showInputDialog(null, "Introduce el NUEVO nombre del archivo",
							ficheroSelec);
					if (!(nombreCarpeta == null))
					{
						// String directorio = direcSelec;
						if (!direcSelec.equals("/"))

							// nombre del directorio a renombrar
							directorio = direcSelec.trim() + ficheroSelec.trim(); // quita blancos a derecha y a
																					// izquierda
						System.out.println(directorio);
						try
						{
							if (cliente.isAvailable())
							{
								cliente.rename(directorio, nombreCarpeta);
								String m = directorio.trim() + " => Se ha modificado correctamente ...";
								JOptionPane.showMessageDialog(null, m);
								txtInformaci�n.setText(m);
								// directorio de trabajo actual
								cliente.changeWorkingDirectory(direcSelec);
								FTPFile[] ff2 = null;
								// obtener ficheros del directorio actual
								ff2 = cliente.listFiles();
								// llenar la lista
								llenarLista(ff2, direcSelec);
							} else
								JOptionPane.showMessageDialog(null,
										nombreCarpeta.trim() + " => No se ha podido renombrar ...");
						} catch (IOException e1)
						{
							e1.printStackTrace();
						}
					}
				} else
				{

					JOptionPane.showMessageDialog(null, ficheroSelec + "--> No es un ARCHIVO");

				}
			}
		});// Bot�n renombrar archivo
		btnBorrarCarpeta.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (!ficheroSelec.contains("(DIR)"))
				{

					JOptionPane.showMessageDialog(null, ficheroSelec + "--> Este elemento NO es una CARPETA");

				} else
				{
					String[] eliminarFichero = ficheroSelec.split(" ");
					String nombreCarpeta = JOptionPane.showInputDialog(null,
							"Introduce el nombre del directorio a eliminar", eliminarFichero[1]);
					if (!(nombreCarpeta == null))
					{
						String directorio = direcSelec;
						if (!direcSelec.equals("/"))
							directorio = directorio + "/";
						// nombre del directorio a eliminar
						directorio += nombreCarpeta.trim(); // quita blancos a derecha y a izquierda
						try
						{
							if (cliente.removeDirectory(directorio))
							{
								String m = nombreCarpeta.trim() + " => Se ha eliminado correctamente ...";
								JOptionPane.showMessageDialog(null, m);
								txtInformaci�n.setText(m);								
								// directorio de trabajo actual
								cliente.changeWorkingDirectory(direcSelec);
								FTPFile[] ff2 = null;
								// obtener ficheros del directorio actual
								ff2 = cliente.listFiles();
								// llenar la lista
								llenarLista(ff2, direcSelec);
							} else
								JOptionPane.showMessageDialog(null,
										nombreCarpeta.trim() + " => No se ha podido eliminar ...");
						} catch (IOException e1)
						{
							e1.printStackTrace();
						}
					}// final del if
				}
				
			}
		});
		// final del bot�n Eliminar Carpeta
		btnCargarArchivo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser jfc;
				File file;
				jfc = new JFileChooser();
				// solo se pueden seleccionar ficheros
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				// t�tulo de la ventana
				jfc.setDialogTitle("Selecciona el fichero a subir al servidor FTP");
				// se muestra la ventana
				int returnVal = jfc.showDialog(jfc, "Cargar");
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					// fichero seleccionado
					file = jfc.getSelectedFile();
					// nombre completo del fichero
					String archivo = file.getAbsolutePath();
					// solo nombre del fichero
					String nombreArchivo = file.getName();
					try
					{
						Subir(archivo, nombreArchivo);
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		}); // Fin bot�n subir
		btnDescargar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String directorio = direcSelec;
				if (!direcSelec.equals("/"))
					directorio = directorio + "/";
				if (!direcSelec.equals(""))
				{
					Descargar(directorio + ficheroSelec, ficheroSelec);
				}
			}
		}); // Fin bot�n descargar
		btnBorrarArchivo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

				String directorio = direcSelec;
				if (!direcSelec.equals("/"))
					directorio = directorio + "/";
				if (!direcSelec.equals(""))
				{

					Borrar(directorio + ficheroSelec, ficheroSelec);

				}
			}
		});

		
		btnVolver.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				String directorio = "";
				directorio = direcSelec + directorio;
				Volver(directorio);

			}
		});
	} 
		

	private static void llenarLista(FTPFile[] files, String direc2)
	{
		if (files == null)
			return;
		// se crea un objeto DefaultListModel
		DefaultListModel<String> lista = new DefaultListModel<String>();
		lista = new DefaultListModel<String>();
		// se definen propiedades para la lista, color y tipo de fuente

		listaDirec.setForeground(Color.blue);
		Font fuente = new Font("Courier", Font.PLAIN, 12);
		listaDirec.setFont(fuente);
		// se eliminan los elementos de la lista
		listaDirec.removeAll();
		try
		{
			// se establece el directorio de trabajo actual
			cliente.changeWorkingDirectory(direc2);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		direcSelec = direc2; // directorio actual
		// se a�ade el directorio de trabajo al listmodel,
		// primerelementomodeloLista.addElement(direc2);
		// se recorre el array con los ficheros y directorios
		for (int i = 0; i < files.length; i++)
		{
			if (!(files[i].getName()).equals(".") && !(files[i].getName()).equals(".."))
			{
				// nos saltamos los directorios . y ..
				// Se obtiene el nombre del fichero o directorio
				String f = files[i].getName();
				// Si es directorio se a�ade al nombre (DIR)
				if (files[i].isDirectory())
					f = "(DIR) " + f;
				// se a�ade el nombre del fichero o directorio al listmodel
				lista.addElement(f);
			} // fin if
		} // fin for
		try
		{
			// se asigna el listmodel al JList,
			// se muestra en pantalla la lista de ficheros y direc
			listaDirec.setModel(lista);
		} catch (NullPointerException n)
		{
			; // Se produce al cambiar de directorio
		}
	}// Fin llenarLista

	private boolean Subir(String archivo, String soloNombre) throws IOException
	{
		cliente.setFileType(FTP.BINARY_FILE_TYPE);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(archivo));
		boolean correcto = false;
		// directorio de trabajo actual
		cliente.changeWorkingDirectory(direcSelec);
		if (cliente.storeFile(soloNombre, in))
		{
			String s = " " + soloNombre + " => Subido correctamente...";
			txtInformaci�n.setText(s);
			txtActualizarDirectorio.setText("Se va a actualizar el �rbol de directorios...");
			JOptionPane.showMessageDialog(null, s);
			FTPFile[] ff2 = null;
			// obtener ficheros del directorio actual
			ff2 = cliente.listFiles();
			// llenar la lista con los ficheros del directorio actual
			llenarLista(ff2, direcSelec);
			correcto = true;
		} else
			txtInformaci�n.setText("No se ha podido subir... " + soloNombre);
		return correcto;
	}// final de SubirFichero

	private void Descargar(String NombreCompleto, String nombreFichero)
	{
		File file;
		String ruta = "";
		String carpetaDestino = "";
		JFileChooser jfc = new JFileChooser();
		// solo se pueden seleccionar directorios
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// t�tulo de la ventana
		jfc.setDialogTitle("Selecciona el Directorio donde Descargar el Fichero");
		int returnVal = jfc.showDialog(null, "Descargar");
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			file = jfc.getSelectedFile();
			// obtener carpeta de destino
			carpetaDestino = (file.getAbsolutePath()).toString();
			// construimos el nombre completo que se crear� en nuestro disco
			ruta = carpetaDestino + File.separator + nombreFichero;
			try
			{
				cliente.setFileType(FTP.BINARY_FILE_TYPE);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(ruta));
				if (cliente.retrieveFile(NombreCompleto, out))
					JOptionPane.showMessageDialog(null, nombreFichero + " => Se ha descargado correctamente ...");
				else
					JOptionPane.showMessageDialog(null, nombreFichero + " => No se ha podido descargar ...");
				out.close();
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	} // Final de DescargarFichero

	private void Borrar(String NombreCompleto, String nombreFichero)
	{

		if (ficheroSelec.contains("(DIR)"))
		{

			JOptionPane.showMessageDialog(null, ficheroSelec + "--> Este elemento NO es un ARCHIVO");

		} else
		{
			int elegir = JOptionPane.showConfirmDialog(null, "�Desea eliminar el fichero seleccionado?");
			if (elegir == JOptionPane.OK_OPTION)
			{

				try
				{
					if (cliente.deleteFile(NombreCompleto))
					{
						String m = nombreFichero + " => Eliminado correctamente... ";
						JOptionPane.showMessageDialog(null, m);
						txtInformaci�n.setText(m);
						// directorio de trabajo actual
						cliente.changeWorkingDirectory(direcSelec);
						FTPFile[] ff2 = null;
						// obtener ficheros del directorio actual
						ff2 = cliente.listFiles();
						// llenar la lista con los ficheros del directorio actual
						llenarLista(ff2, direcSelec);
					} else
						JOptionPane.showMessageDialog(null, nombreFichero + " => No se ha podido eliminar ...");
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		}
	}// Final de BorrarFichero
	
//Funci�n Volver
	private void Volver(String directorio)
	{
		String directorioFinal = "";
		if(!txtArbolDirectoriosConstruido.getText().equals("<< �RBOL DE DIRECTORIOS >>")) 
		{
			for(int i = 0; i < (txtArbolDirectoriosConstruido.getText().split("/").length - 1); i++) 
			{
	            directorioFinal = directorioFinal+txtArbolDirectoriosConstruido.getText().split("/")[i]+"/";
	        }
			
			direcSelec = directorioFinal;
			if(direcSelec.equals("")) {
				direcSelec = "/";
			}
			System.out.println("esto es los que muestra "+direcSelec);
	        
	        try
			{
				// directorio de trabajo actual
				cliente.changeWorkingDirectory(direcSelec);
				FTPFile[] ff2 = null;
				// obtener ficheros del directorio actual
				ff2 = cliente.listFiles();
				// llenar la lista
				llenarLista(ff2, direcSelec);
				if(direcSelec.equals("/")) 
				{
					txtArbolDirectoriosConstruido.setText("<< �RBOL DE DIRECTORIOS >>");
				}
				else 
				{
					txtArbolDirectoriosConstruido.setText(direcSelec);
				}
				
				
			} catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		else 
		{
			JOptionPane.showMessageDialog(null,"Est�s en el directorio Raiz.");
		}
		
	}

}