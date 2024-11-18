package com.example.todolistkids

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.todolistkids.databinding.FragmentNovaPlanilhaTarefasBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalTime

class Fragment1_NovaPlanilhaTarefas(var taskItem: TaskItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNovaPlanilhaTarefasBinding
    private lateinit var modeloTarefas: ModeloTarefas
    private var dueTime: LocalTime? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        if (taskItem != null){
            binding.taskTitle.text = "Editar Tarefa"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(taskItem!!.name)
            binding.desc.text = editable.newEditable(taskItem!!.desc)
            if (taskItem!!.dueTime() != null){
                dueTime = taskItem!!.dueTime()!!
                updateTimeButtonText()
            }
        }
        else{
            binding.taskTitle.text = "Nova Tarefa"
        }
        modeloTarefas = ViewModelProvider(activity).get(ModeloTarefas::class.java)
        binding.saveButton.setOnClickListener {
            saveAction()
        }

        binding.timePickerButton.setOnClickListener {
            openTimePicker()
        }
    }

    private fun openTimePicker() {
        if(dueTime == null)
            dueTime = LocalTime.now()
        val listener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            dueTime = LocalTime.of(selectedHour,selectedMinute)
            updateTimeButtonText()
        }
        val dialog = TimePickerDialog(activity, listener,dueTime!!.hour, dueTime!!.minute, true)
        dialog.setTitle("Tarefa Vencida")
        dialog.show()
    }

    private fun updateTimeButtonText() {
        binding.timePickerButton.text = String.format("%02d:%02d",dueTime!!.hour,dueTime!!.minute)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNovaPlanilhaTarefasBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun saveAction() {
        val nome = binding.name.text.toString()
        val descricao = binding.desc.text.toString()
        val dueTimeString = if(dueTime == null)null else TaskItem.timeFormatter.format(dueTime)
        if(taskItem == null){
            val novaTarefa = TaskItem(nome,descricao,dueTimeString,null)
            modeloTarefas.addTaskItem(novaTarefa)
        }
        else{
            taskItem!!.name = nome
            taskItem!!.desc = descricao
            taskItem!!.dueTimeString = dueTimeString
            modeloTarefas.updateTaskItem(taskItem!!)
        }
        binding.name.setText("")
        binding.desc.setText("")
        dismiss()
    }

}